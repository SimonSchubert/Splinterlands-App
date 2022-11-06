package com.example.splinterlandstest.battles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.painter.Painter
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.compose.rememberAsyncImagePainter
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import org.koin.android.ext.android.get
import kotlin.time.Duration.Companion.seconds


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BattlesFragment : Fragment() {

    val cache: Cache = get()
    val requests: Requests = get()

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private val viewModel by viewModels<BattlesViewModel> {
        BattlesViewModelFactory(activityViewModel.playerName, cache, requests)
    }

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

        viewModel.loadBattles(activityViewModel.playerName)
    }
}

@Composable
fun Content(state: BattlesViewState) {
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val context = LocalContext.current

    SwipeRefresh(
        state = swipeRefreshState,
        swipeEnabled = state !is BattlesViewState.Loading,
        onRefresh = {
            state.onRefresh(context)
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.bg_balance),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            when (state) {
                is BattlesViewState.Loading -> LoadingScreen()
                is BattlesViewState.Success -> ReadyScreen(
                    battles = state.battles,
                    playerName = state.playerName,
                    playerRating = state.playerRating,
                    focusChests = state.focusChests,
                    focusChestUrl = state.focusChestUrl,
                    focusEndTimestamp = state.focusEndTimestamp,
                    seasonChests = state.seasonChests,
                    seasonChestUrl = state.seasonChestUrl,
                    seasonEndTimestamp = state.seasonEndTimestamp
                )
                is BattlesViewState.Error -> ErrorScreen()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    CircularProgressIndicator()
}

@Composable
fun ReadyScreen(
    battles: List<BattleViewState>,
    playerName: String,
    playerRating: String,
    focusChests: Int,
    focusChestUrl: String,
    focusEndTimestamp: Long,
    seasonChests: Int,
    seasonChestUrl: String,
    seasonEndTimestamp: Long
) {
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {

        item {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = playerName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = playerRating,
                color = Color.White
            )
        }

        item {
            ChestRow(
                focusChests,
                focusChestUrl,
                focusEndTimestamp,
                seasonChests,
                seasonChestUrl,
                seasonEndTimestamp
            )
        }

        items(battles) { battle ->
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
    val currentTimestamp = remember { mutableStateOf(System.currentTimeMillis().div(1_000)) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            currentTimestamp.value = System.currentTimeMillis().div(1_000)
        }
    }

    Row {

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
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://splinterlands.com/?p=battle&id=${battle.id}"))
            startActivity(context, browserIntent, null)
        }) {

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = battle.player1Name,
                fontSize = 16.sp,
                color = Color.White
            )

            Text(
                text = battle.player1Rating,
                color = Color.White
            )

            FlowRow(
                mainAxisAlignment = MainAxisAlignment.End,
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp
            ) {
                battle.player1CardUrls.forEach { url ->
                    val image: Painter = rememberAsyncImagePainter(url.url)
                    Image(
                        painter = image,
                        modifier = Modifier
                            .size(30.dp)
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

        Column(
            modifier = Modifier.width(64.dp),
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
                            radius = size.maxDimension
                                .div(2)
                                .plus(10.dp.value)
                        )
                    }
            )

            Row(modifier = Modifier) {
                battle.rulesetUrls.forEach { url ->
                    val image: Painter = rememberAsyncImagePainter(url)
                    Image(
                        painter = image,
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

            Text(
                text = battle.player2Name,
                fontSize = 16.sp,
                color = Color.White
            )

            Text(
                text = battle.player2Rating,
                color = Color.White
            )

            FlowRow(
                mainAxisAlignment = MainAxisAlignment.Start,
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp
            ) {
                battle.player2CardUrls.forEach { url ->
                    val image: Painter = rememberAsyncImagePainter(url.url)
                    Image(
                        painter = image,
                        modifier = Modifier
                            .size(30.dp)
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
    }
}

@Composable
fun RewardChest(chestUrl: String, chestText: String, currentTimestamp: Long, endTimestamp: Long) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        val image: Painter = rememberAsyncImagePainter(chestUrl)
        Image(
            painter = image,
            modifier = Modifier.size(100.dp),
            contentDescription = ""
        )

        Text(
            text = chestText,
            color = Color.White
        )

        Text(
            text = "${(endTimestamp - currentTimestamp).seconds}",
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
