@file:OptIn(ExperimentalUnitApi::class)

package com.example.splinterlandstest.balances

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.rewards.RewardItem
import java.text.NumberFormat
import java.util.*


/**
 * Balances fragment
 */
class BalancesFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.balances)

        return ComposeView(requireContext()).apply {
            setContent {
                BalancesGrid(activityViewModel.playerName)
            }
        }
    }
}

private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

@Composable
fun BalancesGrid(
    player: String,
    viewModel: BalancesFragmentViewModel = viewModel(factory = BasicGroupModelFactory(LocalContext.current, player))
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
                    BalanceItem(state[balance])
                }
            }
        }
    }
}

@Composable
fun BalanceItem(balance: Requests.BalancesResponse) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val image: Painter = painterResource(id = balance.getDrawableResource())
        Image(
            painter = image,
            modifier = Modifier.size(50.dp),
            contentDescription = ""
        )

        Text(
            text = numberFormat.format(balance.balance.toInt()),
            modifier = Modifier.padding(top = 12.dp),
            color = Color.White,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview
fun BalancesPreview() {
    Column {
        BalanceItem(balance = Requests.BalancesResponse("", "LICENSE", 1f))
        BalanceItem(balance = Requests.BalancesResponse("", "CHAOS", 6f))
        BalanceItem(balance = Requests.BalancesResponse("", "NIGHTMARE", 18f))
        BalanceItem(balance = Requests.BalancesResponse("", "PLOT", 3f))

    }
}