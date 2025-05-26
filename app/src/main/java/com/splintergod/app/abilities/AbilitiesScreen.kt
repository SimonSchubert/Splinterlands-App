@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.abilities

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
import com.splintergod.app.models.Ability
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AbilitiesScreen(
    navController: NavHostController,
    viewModel: AbilitiesViewModel = koinViewModel()
) {
    val screenState by viewModel.state.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // ViewModel's init block handles initial loading.
    // LaunchedEffect(Unit) { viewModel.refreshAbilities() } // Could be used if init didn't load.

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshAbilities() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Abilities") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues -> // Renamed to paddingValues to avoid clash if Content uses 'it'
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .pullRefresh(pullRefreshState)
        ) {
            Content(state = screenState) // Pass the screenState to Content
            SplinterPullRefreshIndicator(
                pullRefreshState
            )
        }
    }
}

@Composable
fun Content(state: AbilitiesViewState) {
    // This Composable now just defines the content based on the state.
    // Pull-to-refresh is handled by the caller (AbilitiesScreen).
    Box(
        modifier = Modifier.fillMaxSize(), // Content itself fills the Box provided by AbilitiesScreen
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(resId = R.drawable.bg_gate)

        when (state) {
            is AbilitiesViewState.Loading -> LoadingScreen(R.drawable.loading)
            is AbilitiesViewState.Success -> ReadyScreen(abilities = state.abilities)
            // ErrorScreen from composables might not need onRefresh, or could take a refresh lambda
            is AbilitiesViewState.Error -> ErrorScreen(message = state.message) // ViewModel handles refresh via pullRefreshState
        }
        // SplinterPullRefreshIndicator is now in AbilitiesScreen
    }
}

@Composable
fun ReadyScreen(
    abilities: List<Ability>
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp), // Added padding
        columns = GridCells.Adaptive(minSize = 300.dp)
    ) {
        items(abilities.size) { index ->
            AbilityItem(abilities[index])
        }
    }
}

@Composable
fun AbilityItem(ability: Ability) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) { // Added vertical padding
        ListItem(
            icon = {
                AsyncImage(
                    model = ability.getImageUrl(),
                    modifier = Modifier.size(50.dp, 50.dp),
                    contentDescription = ""
                )
            },
            text = {
                Text(
                    text = ability.name.uppercase(),
                    color = Color(0XFFffa500),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            })

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = ability.desc,
            color = Color.White
        )
    }
}

@Composable
@Preview
fun AbilitiesPreview() { // Renamed for clarity
    val mockAbilities = listOf(
        Ability(
            name = "Flying",
            desc = "Has an increased chance of evading Melee or Ranged attacks from Monsters who do not have the Flying ability."
        )
    )
    // ReadyScreen(abilities = mockAbilities) // Scaffold padding makes direct preview tricky
}
