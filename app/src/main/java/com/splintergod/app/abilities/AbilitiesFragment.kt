@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.abilities

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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.models.Ability
import org.koin.androidx.viewmodel.ext.android.viewModel


class AbilitiesFragment : Fragment() {

    private val viewModel: AbilitiesViewModel by viewModel()

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
        BackgroundImage(resId = R.drawable.bg_gate)

        when (state) {
            is AbilitiesViewState.Loading -> LoadingScreen(R.drawable.loading)
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
            .fillMaxSize(),
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
fun RewardsPreview() {
    val mockAbilities = listOf(
        Ability("", "")
    )
    ReadyScreen(abilities = mockAbilities)
}