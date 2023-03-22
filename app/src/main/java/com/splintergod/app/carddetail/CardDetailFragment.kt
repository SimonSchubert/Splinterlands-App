@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.carddetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
import java.util.*


/**
 * Balances fragment
 */
class CardDetailFragment : Fragment() {

    private val viewModel: CardDetailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.cardName.observe(viewLifecycleOwner) {
            activity?.title = it
        }

        return ComposeView(requireContext()).apply {
            setContent {
                Content(viewModel.state.collectAsState().value)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadCard()
    }
}

@Composable
fun Content(state: CardDetailViewState) {
    val context = LocalContext.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { state.onRefresh(context) })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        BackgroundImage(resId = R.drawable.bg_balance)

        when (state) {
            is CardDetailViewState.Loading -> LoadingScreen(R.drawable.balances)
            is CardDetailViewState.Success -> ReadyScreen(state)
            is CardDetailViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun ReadyScreen(state: CardDetailViewState.Success) {
    Column(Modifier.verticalScroll(rememberScrollState())) {

        Spacer(Modifier.height(20.dp))

        var selectedLevel by remember {
            mutableStateOf(state.card.level)
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = state.card.getImageUrl(state.cardDetail, selectedLevel),
                placeholder = painterResource(state.card.getPlaceholderDrawable()),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(200.dp)
            )
        }

        Spacer(Modifier.height(30.dp))

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val allAbilitiesCount = state.cardDetail.stats.abilities?.flatten()?.count() ?: 0

            repeat(state.cardDetail.stats.health?.size ?: 0) { rowIndex ->

                if (rowIndex == 0) {
                    StatsHeaderRow(state, allAbilitiesCount)
                }

                val backgroundColor = if(selectedLevel == rowIndex + 1) {
                    Color.Black.copy(alpha = 0.3f)
                } else {
                    Color.Transparent
                }
                Row(
                    Modifier.clickable {
                        selectedLevel = rowIndex + 1
                    }.background(backgroundColor),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val ownedCount = state.card.regularLevels.count { it == rowIndex + 1 } + state.card.goldLevels.count { it == rowIndex + 1 }
                    if(ownedCount > 0) {
                        Box(
                            contentAlignment = Alignment.TopCenter) {
                            Image(
                                painterResource(id = R.drawable.quantity_banner),
                                contentDescription = "",
                                modifier = Modifier.width(30.dp)
                            )
                            Text(
                                modifier = Modifier.offset(y = 2.dp),
                                text = ownedCount.toString(),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    } else {
                        Spacer(Modifier.width(30.dp))
                    }

                    Text(
                        modifier = Modifier.width(40.dp),
                        color = Color.Yellow,
                        fontSize = 22.sp,
                        text = (rowIndex + 1).toString(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.width(10.dp))

                    if (state.cardDetail.stats.magic?.any { it > 0 } == true) {
                        StatsText(
                            text = state.cardDetail.stats.magic?.get(rowIndex).toString()
                        )
                    }

                    if (state.cardDetail.stats.attack?.any { it > 0 } == true) {
                        StatsText(
                            text = state.cardDetail.stats.attack?.get(rowIndex).toString()
                        )
                    }

                    if (state.cardDetail.stats.ranged?.any { it > 0 } == true) {
                        StatsText(
                            text = state.cardDetail.stats.ranged?.get(rowIndex).toString()
                        )
                    }

                    if (state.cardDetail.stats.speed?.any { it > 0 } == true) {
                        StatsText(
                            text = state.cardDetail.stats.speed?.get(rowIndex).toString()
                        )
                    }

                    if (state.cardDetail.stats.armor?.any { it > 0 } == true) {
                        StatsText(
                            text = state.cardDetail.stats.armor?.get(rowIndex).toString()
                        )
                    }

                    if (state.cardDetail.stats.health?.any { it > 0 } == true) {
                        StatsText(
                            text = state.cardDetail.stats.health?.get(rowIndex).toString()
                        )
                    }

                    val rowAbilities = state.cardDetail.stats.abilities?.take(rowIndex + 1)?.flatten()

                    repeat(allAbilitiesCount) { index ->

                        val abilityImageUrl = state.abilities.firstOrNull { it.name == rowAbilities?.getOrNull(index) }

                        if (abilityImageUrl != null) {
                            AsyncImage(
                                model = abilityImageUrl.getImageUrl(),
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .padding(3.dp)
                                    .width(34.dp)
                            )
                        } else {
                            Spacer(Modifier.width(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsHeaderRow(state: CardDetailViewState.Success, allAbilities: Int) {

    Row {

        Spacer(Modifier.width(30.dp))

        Text(
            modifier = Modifier.width(40.dp),
            color = Color.Yellow,
            fontSize = 22.sp,
            text = "LVL"
        )

        Spacer(Modifier.width(10.dp))

        if (state.cardDetail.stats.magic?.any { it > 0 } == true) {
            StatsImage(id = R.drawable.stats_magic)
        }
        if (state.cardDetail.stats.attack?.any { it > 0 } == true) {
            StatsImage(id = R.drawable.stats_melee)
        }
        if (state.cardDetail.stats.ranged?.any { it > 0 } == true) {
            StatsImage(id = R.drawable.stats_ranged)
        }
        if (state.cardDetail.stats.speed?.any { it > 0 } == true) {
            StatsImage(id = R.drawable.stats_speed)
        }
        if (state.cardDetail.stats.armor?.any { it > 0 } == true) {
            StatsImage(id = R.drawable.stats_defense)
        }
        if (state.cardDetail.stats.health?.any { it > 0 } == true) {
            StatsImage(id = R.drawable.stats_health)
        }
        repeat(allAbilities) {
            Spacer(Modifier.width(40.dp))
        }
    }
}

@Composable
fun StatsImage(id: Int) {
    Image(
        painterResource(id = id),
        contentDescription = "",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .padding(3.dp)
            .width(34.dp)
    )
}

@Composable
fun StatsText(text: String) {

    Text(
        modifier = Modifier.width(40.dp),
        color = Color.White,
        fontSize = 22.sp,
        text = text,
        textAlign = TextAlign.Center
    )
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
    // ReadyScreen(mockBalances)
}