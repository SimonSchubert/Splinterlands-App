@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.focuses

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
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
import com.splintergod.app.models.Focus
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FocusesScreen(
    navController: NavHostController,
    viewModel: FocusesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRewards()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focuses") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Content(state = state)
    }
}

@Composable
fun Content(state: FocusesViewState) {

    val pullRefreshState = rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = { state.onRefresh() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(resId = R.drawable.bg_gate)

        when (state) {
            is FocusesViewState.Loading -> LoadingScreen(R.drawable.faq)
            is FocusesViewState.Success -> ReadyScreen(focuses = state.focuses)
            is FocusesViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState, state.isRefreshing)
    }
}

@Composable
fun ReadyScreen(
    focuses: List<Focus>
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp), // Added padding
        columns = GridCells.Adaptive(minSize = 300.dp)
    ) {
        items(focuses.size) { index ->
            FocusItem(focuses[index])
        }
    }
}

@Composable
fun FocusItem(focus: Focus) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) { // Reduced horizontal padding, added vertical

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = focus.name.replace("_", " ").uppercase(),
                color = Color(0XFFffa500),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            focus.data.getSplinterDrawable()?.let { splinterDrawable ->
                Image(
                    painter = painterResource(id = splinterDrawable),
                    modifier = Modifier.size(24.dp, 24.dp),
                    contentDescription = ""
                )
            }

            focus.data.getAbilityUrls().forEach { url ->
                AsyncImage(
                    model = url,
                    modifier = Modifier.size(32.dp, 32.dp),
                    contentDescription = ""
                )
            }
        }

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = focus.data.description,
            color = Color.White
        )
    }
}

@Composable
@Preview
fun FocusesPreview() { // Renamed from RewardsPreview for clarity
    val mockFocuses = emptyList<Focus>()
    // ReadyScreen(focuses = mockFocuses) // Scaffold padding makes direct preview of ReadyScreen tricky
    // For a more useful preview, you might wrap ReadyScreen in a Box or similar with padding.
}
