@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.splintergod.app.MainActivity
import com.splintergod.app.R
import com.splintergod.app.carddetail.CardDetailFragment
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.ErrorScreen
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import com.splintergod.app.models.*
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Rewards fragment
 */
class RewardsFragment : Fragment() {

    private val viewModel: RewardsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.rewards)

        return ComposeView(requireContext()).apply {
            setContent {
                Content(viewModel.state.collectAsState().value) {
                    viewModel.session.currentCardDetailId = it.cardId.toString()
                    viewModel.session.currentCardDetailLevel = 1
                    (requireActivity() as MainActivity).setCurrentFragment(CardDetailFragment())
                }
            }
        }
    }
}

@Composable
fun Content(state: RewardsViewState, onClickCard: (id: CardReward) -> Unit) {

    val pullRefreshState = rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = { state.onRefresh() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(resId = R.drawable.bg_balance)

        when (state) {
            is RewardsViewState.Loading -> LoadingScreen(R.drawable.chest)
            is RewardsViewState.Success -> ReadyScreen(rewardGroups = state.rewardsGroups, onClickCard)
            is RewardsViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState, state.isRefreshing)
    }
}

@Composable
fun ReadyScreen(
    rewardGroups: List<RewardGroup>, onClickCard: (id: CardReward) -> Unit
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
                RewardItem(rewardGroup.rewards[balance], onClickCard)
            }
        }
    }
}

@Composable
fun RewardItem(reward: Reward, onClickCard: (id: CardReward) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = if (reward is CardReward) {
                Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .clickable {
                        onClickCard(reward)
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

@Composable
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
    ReadyScreen(rewardGroups = mockRewards) {}
}