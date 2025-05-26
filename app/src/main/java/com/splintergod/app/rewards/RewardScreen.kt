@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.ErrorScreen
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import com.splintergod.app.models.CardReward
import com.splintergod.app.models.CreditsReward
import com.splintergod.app.models.DecReward
import com.splintergod.app.models.GlintReward
import com.splintergod.app.models.GoldPotionReward
import com.splintergod.app.models.LegendaryPotionReward
import com.splintergod.app.models.MeritsReward
import com.splintergod.app.models.PackReward
import com.splintergod.app.models.Reward
import com.splintergod.app.models.RewardGroup
import com.splintergod.app.models.SPSReward
import org.koin.androidx.compose.koinViewModel


@Composable
fun RewardScreen(
    navController: NavHostController,
    viewModel: RewardsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.onRefresh() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(resId = R.drawable.bg_balance)

        when (state) {
            is RewardsViewState.Loading -> LoadingScreen(R.drawable.chest)
            is RewardsViewState.Success -> ReadyScreen(
                navController = navController, // Pass navController
                rewardGroups = state.rewardsGroups
            )

            is RewardsViewState.Error -> ErrorScreen(message = state.message)
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun ReadyScreen(
    navController: NavHostController, // Added navController
    rewardGroups: List<RewardGroup>
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 112.dp)
    ) {
        rewardGroups.forEach { rewardGroup ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(4.dp),
                    text = rewardGroup.player.uppercase() + "\n" + rewardGroup.getFormattedDateShort(),
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
            items(rewardGroup.rewards.size) { balance ->
                RewardItem(
                    navController = navController, // Pass navController
                    reward = rewardGroup.rewards[balance]
                )
            }
        }
    }
}

@Composable
fun RewardItem(
    navController: NavHostController, // Added navController
    reward: Reward
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = if (reward is CardReward) {
                Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("card_detail/${reward.cardId}/1") // Updated navigation
                    }
            } else {
                Modifier
                    .padding(16.dp)
                    .size(50.dp)
            },
            model = getModel(reward),
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(4.dp),
            text = reward.getTitle(),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getModel(reward: Reward): Any {
    return if (reward is CardReward) {
        reward.url
    } else {
        when (reward) {
            is SPSReward -> R.drawable.asset_sps
            is CreditsReward -> R.drawable.asset_credits
            is DecReward -> R.drawable.asset_dec
            is GoldPotionReward -> R.drawable.asset_potion_gold
            is LegendaryPotionReward -> R.drawable.asset_potion_legendary
            is MeritsReward -> R.drawable.asset_merits
            is PackReward -> R.drawable.asset_pack_chaos
            is GlintReward -> R.drawable.asset_pack_chaos
            else -> throw Exception()
        }
    }
}

@Composable
@Preview
fun RewardsPreview() {
    val mockRewards = listOf(
        RewardGroup(
            "date", listOf(
                DecReward(12),
                PackReward,
                GoldPotionReward(5)
            )
        )
    )
    // This preview won't work directly with NavHostController,
    // but the structure is for compilation and basic layout check.
    // For full preview, you might need to mock NavHostController or adjust the Composable.
    // ReadyScreen(navController = rememberNavController(), rewardGroups = mockRewards)
}
