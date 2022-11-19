@file:OptIn(ExperimentalUnitApi::class, ExperimentalMaterialApi::class)

package com.example.splinterlandstest.rulesets

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.splinterlandstest.models.Ruleset
import org.koin.android.ext.android.get


/**
 * Rewards fragment
 */
class RulesetsFragment : Fragment() {

    val cache: Cache = get()
    private val requests: Requests = get()

    private val viewModel by viewModels<RulesetsViewModel> {
        RulesetsViewModelFactory(cache, requests)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.rulesets)

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
fun Content(state: RulesetsViewState) {
    val refreshing = state is RulesetsViewState.Loading
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
            is RulesetsViewState.Loading -> LoadingScreen()
            is RulesetsViewState.Success -> ReadyScreen(rulesets = state.rulesets)
            is RulesetsViewState.Error -> ErrorScreen()
        }

        if (!refreshing) {
            PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
fun ReadyScreen(
    rulesets: List<Ruleset>
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 300.dp)
    ) {
        items(rulesets.size) { index ->
            RulesetItem(rulesets[index])
        }
    }
}

@Composable
fun RulesetItem(ruleset: Ruleset) {
    Column {

        ListItem(icon = {
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
                    fontSize = TextUnit(18f, TextUnitType.Sp),
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
    val mockRulesets = emptyList<Ruleset>()
    ReadyScreen(rulesets = mockRulesets)
}