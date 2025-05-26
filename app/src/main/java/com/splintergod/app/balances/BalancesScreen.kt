@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.balances

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.splintergod.app.models.Balances
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

private val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.US)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BalancesScreen(
    navController: NavHostController,
    viewModel: BalancesViewModel = koinViewModel()
) {
    val screenState by viewModel.state.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // ViewModel's init block calls refreshBalances()
    // LaunchedEffect(Unit) { viewModel.refreshBalances() } // Not needed due to init

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshBalances() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Balances") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .pullRefresh(pullRefreshState)
        ) {
            Content(state = screenState)
            SplinterPullRefreshIndicator(pullRefreshState)
        }
    }
}

@Composable
fun Content(state: BalancesViewState) {
    // Pull-to-refresh is handled by the caller (BalancesScreen).
    // Context is no longer needed here for onRefresh.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        BackgroundImage(resId = R.drawable.bg_balance)

        when (state) {
            is BalancesViewState.Loading -> LoadingScreen(R.drawable.balances)
            is BalancesViewState.Success -> ReadyScreen(balances = state.balances)
            is BalancesViewState.Error -> ErrorScreen(message = state.message) // ViewModel handles refresh via pullRefreshState
        }
        // SplinterPullRefreshIndicator is now in BalancesScreen
    }
}

@Composable
fun ReadyScreen(balances: List<Balances>) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp), // Added padding
        columns = GridCells.Adaptive(minSize = 96.dp)
    ) {
        items(balances.size, key = { balances[it].token }) { index -> // Changed key usage
            BalanceItem(balances[index])
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
    // ReadyScreen(mockBalances) // Scaffold padding makes direct preview tricky
}
