@file:OptIn(ExperimentalUnitApi::class, ExperimentalMaterialApi::class)

package com.example.splinterlandstest.focuses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.LoadingScreen
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.example.splinterlandstest.models.Focus
import org.koin.android.ext.android.get


/**
 * Focuses fragment
 */
class FocusesFragment : Fragment() {

    val cache: Cache = get()
    private val requests: Requests = get()

    private val viewModel by viewModels<FocusesViewModel> {
        FocusesViewModelFactory(cache, requests)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.focuses)

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
fun Content(state: FocusesViewState) {
    val refreshing = state is FocusesViewState.Loading
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = { state.onRefresh() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(id = R.drawable.bg_balance),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        when (state) {
            is FocusesViewState.Loading -> LoadingScreen()
            is FocusesViewState.Success -> ReadyScreen(focuses = state.focuses)
            is FocusesViewState.Error -> ErrorScreen()
        }

        if (!refreshing) {
            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
fun ReadyScreen(
    focuses: List<Focus>
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 300.dp)
    ) {
        items(focuses.size) { index ->
            FocusItem(focuses[index])
        }
    }
}

@Composable
fun FocusItem(focus: Focus) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = focus.name.replace("_", " ").uppercase(),
                color = Color(0XFFffa500),
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            if (focus.data.splinter?.isNotEmpty() == true) {

                val image: Painter? = when (focus.data.splinter) {
                    "Fire" -> painterResource(id = R.drawable.element_fire)
                    "Water" -> painterResource(id = R.drawable.element_water)
                    "Death" -> painterResource(id = R.drawable.element_death)
                    "Dragon" -> painterResource(id = R.drawable.element_dragon)
                    "Earth" -> painterResource(id = R.drawable.element_earth)
                    "Life" -> painterResource(id = R.drawable.element_life)
                    else -> null
                }
                if (image != null) {
                    Image(
                        painter = image,
                        modifier = Modifier.size(24.dp, 24.dp),
                        contentDescription = ""
                    )
                }
            }

            if (focus.data.abilities?.isNotEmpty() == true) {
                Row {
                    focus.data.abilities.forEach { ability ->
                        val url = "https://d36mxiodymuqjm.cloudfront.net/website/abilities/ability_${
                            ability.replace(" ", "-")
                        }.png"
                        AsyncImage(
                            model = url,
                            modifier = Modifier.size(32.dp, 32.dp),
                            contentDescription = ""
                        )
                    }
                }
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
@Preview
fun RewardsPreview() {
    val mockFocuses = emptyList<Focus>()
    ReadyScreen(focuses = mockFocuses)
}