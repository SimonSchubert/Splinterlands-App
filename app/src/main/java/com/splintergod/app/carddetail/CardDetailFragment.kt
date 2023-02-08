@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.carddetail

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import java.text.NumberFormat
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

        activity?.title = viewModel.cardName.value

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

        Row {
            AsyncImage(
                model = state.card.imageUrl,
                placeholder = painterResource(state.card.getPlaceholderDrawable()),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(200.dp)
            )

            Image(
                painterResource(id = state.colorIcon),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(48.dp)
            )
        }

        Spacer(Modifier.height(30.dp))

        Column(Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {

        repeat(state.cardDetail.stats.health?.size ?: 0) { index ->

            if(index == 0) {

                Row {

                    Text(
                        modifier = Modifier.width(40.dp),
                        color = Color.Yellow,
                        fontSize = 22.sp,
                        text = "LVL"
                    )

                    Spacer(Modifier.width(10.dp))


                    if(state.cardDetail.stats.magic?.any { it > 0 } == true) {
                        Image(
                            painterResource(id = R.drawable.stats_magic),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.padding(3.dp).width(34.dp)
                        )
                    }
                    if(state.cardDetail.stats.attack?.any { it > 0 } == true) {
                        Image(
                            painterResource(id = R.drawable.stats_melee),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.padding(3.dp).width(34.dp)
                        )
                    }
                    if(state.cardDetail.stats.ranged?.any { it > 0 } == true) {
                        Image(
                            painterResource(id = R.drawable.stats_ranged),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.padding(3.dp).width(34.dp)
                        )
                    }

                    if(state.cardDetail.stats.speed?.any { it > 0 } == true) {
                        Image(
                            painterResource(id = R.drawable.stats_speed),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.padding(3.dp).width(34.dp)
                        )
                    }
                    if(state.cardDetail.stats.armor?.any { it > 0 } == true) {
                        Image(
                            painterResource(id = R.drawable.stats_defense),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.padding(3.dp).width(34.dp)
                        )
                    }

                    if(state.cardDetail.stats.health?.any { it > 0 } == true) {
                        Image(
                            painterResource(id = R.drawable.stats_health),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.padding(3.dp).width(34.dp)
                        )
                    }
                }
            }

            Row {
                Text(
                    modifier = Modifier.width(40.dp),
                    color = Color.Yellow,
                    fontSize = 22.sp,
                    text = (index + 1).toString(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.width(10.dp))

                if(state.cardDetail.stats.magic?.any { it > 0 } == true) {
                    StatsText(
                        text = state.cardDetail.stats.magic?.get(index).toString()
                    )
                }

                if(state.cardDetail.stats.attack?.any { it > 0 } == true) {
                    StatsText(
                        text = state.cardDetail.stats.attack?.get(index).toString()
                    )
                }

                if(state.cardDetail.stats.ranged?.any { it > 0 } == true) {
                    StatsText(
                        text = state.cardDetail.stats.ranged?.get(index).toString()
                    )
                }

                if(state.cardDetail.stats.speed?.any { it > 0 } == true) {
                    StatsText(
                        text = state.cardDetail.stats.speed?.get(index).toString()
                    )
                }

                if(state.cardDetail.stats.armor?.any { it > 0 } == true) {
                    StatsText(
                        text = state.cardDetail.stats.armor?.get(index).toString()
                    )
                }

                if(state.cardDetail.stats.health?.any { it > 0 } == true) {
                    StatsText(
                        text = state.cardDetail.stats.health?.get(index).toString()
                    )
                }
            }
        }
        }

    }
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