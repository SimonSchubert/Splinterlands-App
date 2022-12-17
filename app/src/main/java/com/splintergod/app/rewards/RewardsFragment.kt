@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.ErrorScreen
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import com.splintergod.app.models.CardReward
import com.splintergod.app.models.CreditsReward
import com.splintergod.app.models.DecReward
import com.splintergod.app.models.GoldPotionReward
import com.splintergod.app.models.LegendaryPotionReward
import com.splintergod.app.models.MeritsReward
import com.splintergod.app.models.PackReward
import com.splintergod.app.models.Reward
import com.splintergod.app.models.SPSReward
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
                Content(viewModel.state.collectAsState().value)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadRewards()
    }
}

@Composable
fun Content(state: RewardsViewState) {

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
            is RewardsViewState.Success -> ReadyScreen(rewards = state.rewards)
            is RewardsViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState, state.isRefreshing)
    }
}

@Composable
fun ReadyScreen(
    rewards: List<Reward>
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 96.dp)
    ) {
        items(rewards.size) { balance ->
            RewardItem(rewards[balance])
        }
    }
}

@Composable
fun RewardItem(reward: Reward) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            model = getModel(reward),
            modifier = Modifier.size(100.dp, 140.dp),
            contentDescription = ""
        )

        Text(
            text = reward.getTitle(),
            modifier = Modifier.padding(top = 12.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
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
        DecReward(12),
        PackReward,
        GoldPotionReward(5)
    )
    ReadyScreen(rewards = mockRewards)
}