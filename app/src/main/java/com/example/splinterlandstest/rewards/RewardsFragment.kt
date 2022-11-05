@file:OptIn(ExperimentalUnitApi::class)

package com.example.splinterlandstest.rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.compose.rememberAsyncImagePainter
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.models.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.android.ext.android.get


/**
 * Rewards fragment
 */
class RewardsFragment : Fragment() {

    val cache: Cache = get()
    private val requests: Requests = get()

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel by viewModels<RewardsViewModel> {
        RewardsViewModelFactory(activityViewModel.playerName, cache, requests)
    }

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
    val swipeRefreshState = rememberSwipeRefreshState(false)

    SwipeRefresh(
        state = swipeRefreshState,
        swipeEnabled = state !is RewardsViewState.Loading,
        onRefresh = {
            state.onRefresh()
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.bg_balance),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            when (state) {
                is RewardsViewState.Loading -> LoadingScreen()
                is RewardsViewState.Success -> ReadyScreen(rewards = state.rewards)
                is RewardsViewState.Error -> ErrorScreen()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    CircularProgressIndicator()
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
fun ErrorScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), // scroll for swipe refresh
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Something went wrong",
            color = Color.White
        )
    }
}

@Composable
fun RewardItem(reward: Reward) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val image: Painter = getPainter(reward)
        Image(
            painter = image,
            modifier = Modifier.size(100.dp, 140.dp),
            contentDescription = ""
        )

        Text(
            text = reward.getTitle(),
            modifier = Modifier.padding(top = 12.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun getPainter(reward: Reward): Painter {
    return if (reward is CardReward) {
        rememberAsyncImagePainter(reward.url)
    } else {
        val resId = when (reward) {
            is SPSReward -> R.drawable.sps
            is CreditsReward -> R.drawable.credits
            is DecReward -> R.drawable.dec
            is GoldPotionReward -> R.drawable.gold
            is LegendaryPotionReward -> R.drawable.legendary
            is MeritsReward -> R.drawable.mertis
            is PackReward -> R.drawable.chaos
            else -> throw Exception()
        }
        painterResource(id = resId)
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