@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.ErrorScreen
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import com.splintergod.app.models.Balances
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat
import java.util.*


/**
 * Balances fragment
 */
class BalancesFragment : Fragment() {

    private val viewModel: BalancesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.balances)

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

private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

@Composable
fun Content(state: BalancesViewState) {
    val context = LocalContext.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { state.onRefresh(context) })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {

        BackgroundImage(resId = R.drawable.bg_balance)

        when (state) {
            is BalancesViewState.Loading -> LoadingScreen(R.drawable.balances)
            is BalancesViewState.Success -> ReadyScreen(balances = state.balances)
            is BalancesViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun ReadyScreen(balances: List<Balances>) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 96.dp)
    ) {
        items(balances.size, key = { balances[it].token }) { balance ->
            BalanceItem(balances[balance])
        }
    }
}

@Composable
fun BalanceItem(balance: Balances) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = balance.getDrawableResource(),
            modifier = Modifier.size(50.dp),
            contentDescription = ""
        )

        Text(
            text = numberFormat.format(balance.balance.toInt()),
            modifier = Modifier.padding(top = 12.dp),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview
fun BalancesPreview() {
    val mockBalances = listOf(
        Balances("", "LICENSE", 1f),
        Balances("", "CHAOS", 6f),
        Balances("", "NIGHTMARE", 18f),
        Balances("", "PLOT", 3f)
    )
    ReadyScreen(mockBalances)
}