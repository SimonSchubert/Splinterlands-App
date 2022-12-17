@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.compose.AsyncImage
import com.splintergod.app.MainActivityViewModel
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Collection fragment
 */
class LoginFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = "Login"

        return ComposeView(requireContext()).apply {
            setContent {
                Content(
                    viewModel.state.collectAsState().value,
                    onClickPlayer = { player ->
                        activityViewModel.setPlayer(player)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadPlayerData()
        // activityViewModel.logout()
    }
}

@Composable
fun Content(
    state: LoginViewState,
    onClickPlayer: (player: String) -> Unit,
) {
    val context = LocalContext.current

    var refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        refreshing = true
        state.onRefresh(context)
    })

    if (refreshing && state is LoginViewState.Success) {
        refreshing = state.isRefreshing
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {

        BackgroundImage(resId = R.drawable.bg_login)

        when (state) {
            is LoginViewState.Loading -> LoadingScreen(R.drawable.loading)
            is LoginViewState.Success -> ReadyScreen(
                players = state.players,
                onClickPlayer = onClickPlayer,
                onDeletePlayer = state.onDeletePlayer,
                onAddPlayer = state.onAddPlayer
            )
            is LoginViewState.CouldNotFindPlayerError -> CouldNotFindPlayerErrorScreen(
                player = state.player,
                onAddPlayer = state.onAddPlayer,
                onClickBack = state.onClickBack
            )
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun CouldNotFindPlayerErrorScreen(
    player: String,
    onAddPlayer: (player: String) -> Unit,
    onClickBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier.height(120.dp),
            model = R.drawable.splinterlands_logo,
            contentDescription = ""
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.padding(16.dp),
            backgroundColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Ops, we couldn't find \"$player\"",
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(24.dp))

        AddAccountCard(
            onAddPlayer = onAddPlayer,
            prefilledPlayer = player
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            onClickBack()
        }) {
            Text("Nevermind")
        }
    }
}

@Composable
fun ReadyScreen(
    players: List<LoginViewModel.PlayerRowInfo>,
    onClickPlayer: (player: String) -> Unit,
    onDeletePlayer: (player: String) -> Unit,
    onAddPlayer: (player: String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        Modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier.height(120.dp),
            model = R.drawable.splinterlands_logo,
            contentDescription = ""
        )

        if (players.isEmpty()) {
            Card(
                modifier = Modifier.padding(16.dp),
                backgroundColor = Color.Black.copy(alpha = 0.8f)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Unofficial Splinterlands mobile client",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        } else {
            AccountsList(
                players = players,
                onClick = onClickPlayer,
                onDelete = {
                    onDeletePlayer(it)
                })
        }

        Spacer(Modifier.height(12.dp))

        AddAccountCard(onAddPlayer, "")
    }
}

@Composable
fun AddAccountCard(onAddPlayer: (player: String) -> Unit, prefilledPlayer: String) {
    Card(
        modifier = Modifier.padding(16.dp),
        backgroundColor = Color.Black.copy(alpha = 0.8f)
    ) {

        Column {

            Text(
                text = "ADD ACCOUNT",
                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                var text by remember { mutableStateOf(TextFieldValue(prefilledPlayer)) }
                TextField(
                    value = text,
                    textStyle = TextStyle.Default.copy(color = Color.White),
                    singleLine = true,
                    onValueChange = {
                        text = it
                    },
                    placeholder = {
                        Text(
                            "Player",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            onAddPlayer(text.text)
                        }
                    )
                )

                IconButton(onClick = {
                    onAddPlayer(text.text)
                    text = TextFieldValue("")
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun AccountsList(
    players: List<LoginViewModel.PlayerRowInfo>,
    onClick: (player: String) -> Unit,
    onDelete: (player: String) -> Unit
) {
    Card(
        modifier = Modifier.padding(16.dp),
        backgroundColor = Color.Black.copy(alpha = 0.8f)
    ) {

        Column {

            Text(
                text = "ACCOUNTS",
                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            val textMinWidth = remember {
                mutableStateOf(0.dp)
            }

            players.forEach {
                PlayerItem(it, textMinWidth, onClick) { player ->
                    onDelete(player)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerItem(
    player: LoginViewModel.PlayerRowInfo,
    minTextWidth: MutableState<Dp>,
    onClick: (player: String) -> Unit,
    onDelete: (player: String) -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    ListItem(modifier = Modifier
        .clickable { onClick(player.name) }, text = {

        Row(verticalAlignment = Alignment.CenterVertically) {
            val localDensity = LocalDensity.current

            Text(
                text = player.name.uppercase(),
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        val width = with(localDensity) { coordinates.size.width.toDp() }
                        minTextWidth.value = max(width, minTextWidth.value)
                    }
                    .defaultMinSize(minWidth = minTextWidth.value),
                color = Color.White
            )

            if (player.timeLeft.isNotBlank()) {
                Text(
                    text = "${player.chests}",
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.White
                )

                AsyncImage(
                    model = player.chestUrl,
                    modifier = Modifier.size(40.dp, 40.dp),
                    contentDescription = ""
                )

                Text(text = player.timeLeft, color = Color.White)
            }
        }
    },
        trailing = {
            IconButton(onClick = { showDeleteDialog.value = true }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = Color.White)
            }
        })

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog.value = false
            },
            title = {
                Text("Remove account")
            },
            text = {
                Text("Are you sure you want to remove ${player.name}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(player.name)
                        showDeleteDialog.value = false
                    },
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
@Preview
fun PlayerRowPreview() {

    val minWidth = remember { mutableStateOf(10.dp) }
    PlayerItem(
        player = LoginViewModel.PlayerRowInfo("splinteraccount"),
        minTextWidth = minWidth,
        onClick = {},
        onDelete = {})
}