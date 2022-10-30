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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests


/**
 * Rewards fragment
 */
class RewardsFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.rewards)

        return ComposeView(requireContext()).apply {
            setContent {
                RewardsGrid(activityViewModel.playerName)
            }
        }
    }

}

@Composable
fun RewardsGrid(
    player: String,
    viewModel: RewardsFragmentViewModel = viewModel(factory = RewardModelModelFactory(LocalContext.current, player))
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

        val state = viewModel.state.collectAsState().value

        if (state.isEmpty()) {
            CircularProgressIndicator()
        } else {
            LazyVerticalGrid(
                modifier = Modifier.matchParentSize(),
                columns = GridCells.Adaptive(minSize = 96.dp)
            ) {
                items(state.size) { balance ->
                    RewardItem(state[balance])
                }
            }
        }
    }
}


@Composable
fun RewardItem(reward: Requests.Reward) {
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
fun getPainter(reward: Requests.Reward): Painter {
    return if (reward is Requests.CardReward) {
        rememberAsyncImagePainter(reward.url)
    } else {
        val resId = when (reward) {
            is Requests.SPSReward -> R.drawable.sps
            is Requests.CreditsReward -> R.drawable.credits
            is Requests.DecReward -> R.drawable.dec
            is Requests.GoldPotionReward -> R.drawable.gold
            is Requests.LegendaryPotionReward -> R.drawable.legendary
            is Requests.MeritsReward -> R.drawable.mertis
            is Requests.PackReward -> R.drawable.chaos
            else -> throw Exception()
        }
        painterResource(id = resId)
    }
}