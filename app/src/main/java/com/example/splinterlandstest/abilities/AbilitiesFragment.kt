@file:OptIn(ExperimentalUnitApi::class, ExperimentalMaterialApi::class)

package com.example.splinterlandstest.abilities

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
import com.example.splinterlandstest.models.Ability
import org.koin.android.ext.android.get


class AbilitiesFragment : Fragment() {

    val cache: Cache = get()

    private val viewModel by viewModels<AbilitiesViewModel> {
        AbilitiesViewModelFactory(cache)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.abilities)

        return ComposeView(requireContext()).apply {
            setContent {
                Content(viewModel.state.collectAsState().value)
            }
        }
    }
}

@Composable
fun Content(state: AbilitiesViewState) {
    val pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = { state.onRefresh })

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
            is AbilitiesViewState.Loading -> LoadingScreen()
            is AbilitiesViewState.Success -> ReadyScreen(abilities = state.abilities)
            is AbilitiesViewState.Error -> ErrorScreen()
        }
    }
}

@Composable
fun ReadyScreen(
    abilities: List<Ability>
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 300.dp)
    ) {
        items(abilities.size) { index ->
            AbilityItem(abilities[index])
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
fun AbilityItem(ability: Ability) {
    Column {

        ListItem(icon = {
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
                    fontSize = TextUnit(18f, TextUnitType.Sp),
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
fun RewardsPreview() {
    val mockAbilities = listOf(
        Ability("", "")
    )
    ReadyScreen(abilities = mockAbilities)
}