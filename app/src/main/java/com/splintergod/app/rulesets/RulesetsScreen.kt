@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.rulesets

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
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.splintergod.app.models.Ruleset
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RulesetsScreen(
    navController: NavHostController,
    viewModel: RulesetsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRewards()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rulesets") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val isRefreshing by viewModel.isRefreshing.collectAsState()
        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = { viewModel.onRefresh() }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .pullRefresh(pullRefreshState),
            contentAlignment = Alignment.Center
        ) {
            Content(state = state) // Content now just renders based on state
            SplinterPullRefreshIndicator(pullRefreshState)
        }
    }
}

@Composable
fun Content(state: RulesetsViewState) {
    // The Box with pullRefresh modifier and SplinterPullRefreshIndicator are now in RulesetsScreen
    // This composable just decides what to show based on the state.
    BackgroundImage(resId = R.drawable.bg_gate) // Keep background consistent if it was inside the Box

    when (state) {
        is RulesetsViewState.Loading -> LoadingScreen(R.drawable.faq)
        is RulesetsViewState.Success -> ReadyScreen(rulesets = state.rulesets)
        is RulesetsViewState.Error -> ErrorScreen(message = state.message)
    }
}

@Composable
fun ReadyScreen(
    rulesets: List<Ruleset>
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp), // Added padding
        columns = GridCells.Adaptive(minSize = 300.dp)
    ) {
        items(rulesets.size) { index ->
            RulesetItem(rulesets[index])
        }
    }
}

@Composable
fun RulesetItem(ruleset: Ruleset) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) { // Added vertical padding

        ListItem(
            icon = {
                AsyncImage(
                    model = ruleset.getImageUrl(),
                    modifier = Modifier.size(50.dp, 50.dp),
                    contentDescription = ""
                )
            },
            text = {
                Text(
                    text = ruleset.name.uppercase(),
                    color = Color(0XFFffa500),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            })

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = ruleset.description,
            color = Color.White
        )
    }
}

@Composable
@Preview
fun RulesetsPreview() { // Renamed for clarity
    val mockRulesets = emptyList<Ruleset>()
    // ReadyScreen(rulesets = mockRulesets) // Scaffold padding makes direct preview tricky
}
