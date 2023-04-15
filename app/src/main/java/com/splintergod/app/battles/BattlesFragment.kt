@file:OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)

package com.splintergod.app.battles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.ErrorScreen
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import com.splintergod.app.models.CardFoilUrl
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration.Companion.seconds


class BattlesFragment : Fragment() {

    private val viewModel: BattlesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.battles)

        return ComposeView(requireContext()).apply {
            setContent {
                Content(viewModel.state.collectAsState().value)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadBattles()
    }
}

@Composable
fun Content(state: BattlesViewState) {
    val context = LocalContext.current
    val pullRefreshState =
        rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = { state.onRefresh(context) })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        BackgroundImage(resId = R.drawable.bg_arena)

        when (state) {
            is BattlesViewState.Loading -> LoadingScreen(R.drawable.battles)
            is BattlesViewState.Success -> ReadyScreen(
                battles = state.battles,
                playerName = state.playerName,
                playerRating = state.playerRating,
                energy = state.energy,
                focusChests = state.focusChests,
                focusChestUrl = state.focusChestUrl,
                focusEndTimestamp = state.focusEndTimestamp,
                seasonChests = state.seasonChests,
                seasonChestUrl = state.seasonChestUrl,
                seasonEndTimestamp = state.seasonEndTimestamp
            )

            is BattlesViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun ReadyScreen(
    battles: List<BattleViewState>,
    playerName: String,
    playerRating: String,
    energy: Int,
    focusChests: Int,
    focusChestUrl: String,
    focusEndTimestamp: Long,
    seasonChests: Int,
    seasonChestUrl: String,
    seasonEndTimestamp: Long
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        item(key = "player") {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                modifier = Modifier,
                text = playerName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row {
                Text(
                    text = playerRating,
                    color = Color.White
                )
                Image(
                    painterResource(id = R.drawable.icon_energy),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(20.dp)
                        .padding(start = 4.dp, end = 2.dp)
                )
                Text(
                    text = "${energy}/50",
                    color = Color.White
                )
            }
        }

        item(key = "chests") {
            ChestRow(
                focusChests,
                focusChestUrl,
                focusEndTimestamp,
                seasonChests,
                seasonChestUrl,
                seasonEndTimestamp
            )
        }

        items(items = battles, key = { it.id }) { battle ->
            Battle(battle)
        }
    }
}

@Composable
fun ChestRow(
    focusChests: Int,
    focusChestUrl: String,
    focusEndTimestamp: Long,
    seasonChests: Int,
    seasonChestUrl: String,
    seasonEndTimestamp: Long
) {
    Row {

        val currentTimestamp = remember { mutableStateOf(System.currentTimeMillis().div(1_000)) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(1.seconds)
                currentTimestamp.value = System.currentTimeMillis().div(1_000)
            }
        }

        RewardChest(
            chestUrl = focusChestUrl,
            chestText = "Focus chests: $focusChests",
            currentTimestamp = currentTimestamp.value,
            endTimestamp = focusEndTimestamp
        )

        Spacer(modifier = Modifier.width(24.dp))

        RewardChest(
            chestUrl = seasonChestUrl,
            chestText = "Season chests: $seasonChests",
            currentTimestamp = currentTimestamp.value,
            endTimestamp = seasonEndTimestamp
        )
    }
}

@Composable
fun Battle(battle: BattleViewState) {

    val context = LocalContext.current
    Row(modifier = Modifier
        .padding(top = 12.dp)
        .clickable {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://splinterlands.com/?p=battle&id=${battle.id}"))
            startActivity(context, browserIntent, null)
        }) {

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            BattleLineUp(
                horizontalArrangement = Arrangement.End,
                battle.player1Name,
                battle.player1Rating,
                battle.player1CardUrls
            )
        }

        Column(
            modifier = Modifier.width(74.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = battle.mana,
                color = Color.Black,
                modifier = Modifier
                    .padding(10.dp)
                    .drawBehind {
                        drawCircle(
                            color = if (battle.isWin) {
                                Color(0XFF88FF88)
                            } else {
                                Color(0XFFFF6666)
                            },
                            radius = 32.dp.value
                        )
                    }
            )

            Row {
                battle.rulesetUrls.forEach { url ->
                    AsyncImage(
                        model = url,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 2.dp, end = 2.dp),
                        contentDescription = ""
                    )
                }
            }

            Text(
                text = battle.time,
                color = Color.White
            )

            Text(
                text = battle.matchType,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            BattleLineUp(
                horizontalArrangement = Arrangement.Start,
                battle.player2Name,
                battle.player2Rating,
                battle.player2CardUrls
            )
        }

    }
}

@Composable
fun BattleLineUp(
    horizontalArrangement: Arrangement.Horizontal,
    playerName: String,
    rating: String,
    cardUrls: List<CardFoilUrl>
) {
    Text(
        text = playerName,
        fontSize = 16.sp,
        color = Color.White
    )

    Text(
        text = rating,
        color = Color.White
    )

    FlowRow(
        horizontalArrangement = horizontalArrangement
    ) {
        cardUrls.forEach { url ->
            AsyncImage(
                model = url.url,
                modifier = Modifier
                    .size(30.dp)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp, if (url.isGold) {
                            Color(0XFFffd700)
                        } else {
                            Color.Gray
                        }, CircleShape
                    ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
fun RewardChest(chestUrl: String, chestText: String, currentTimestamp: Long, endTimestamp: Long) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        AsyncImage(
            model = chestUrl,
            modifier = Modifier.size(100.dp),
            contentDescription = ""
        )

        Text(
            text = chestText,
            color = Color.White
        )

        val seconds = (endTimestamp - currentTimestamp).seconds
        val text = if (seconds.isPositive()) {
            "$seconds"
        } else {
            "Claim reward"
        }
        Text(
            text = text,
            color = Color.White
        )
    }
}