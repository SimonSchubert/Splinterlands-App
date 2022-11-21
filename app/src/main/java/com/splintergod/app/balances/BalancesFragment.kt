@file:OptIn(ExperimentalUnitApi::class, ExperimentalMaterialApi::class)

package com.splintergod.app.balances

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.splintergod.app.Cache
import com.splintergod.app.MainActivityViewModel
import com.example.splinterlandstest.R
import com.splintergod.app.Requests
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import com.splintergod.app.models.Balances
import org.koin.android.ext.android.get
import java.text.NumberFormat
import java.util.*


/**
 * Balances fragment
 */
class BalancesFragment : Fragment() {

    val cache: Cache = get()
    private val requests: Requests = get()

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel by viewModels<BalancesViewModel> {
        BalancesViewModelFactory(activityViewModel.playerName, cache, requests)
    }

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
            is BalancesViewState.Loading -> LoadingScreen()
            is BalancesViewState.Success -> ReadyScreen(balances = state.balances)
            is BalancesViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = R.drawable.balances,
            contentDescription = null
        )
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