@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.login

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.splintergod.app.MainActivityViewModel
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = koinViewModel(),
    mainActivityViewModel: MainActivityViewModel = koinViewModel()
) {
    val state by loginViewModel.state.collectAsState()

    // This was how LoginFragment.Content was structured.
    // The original LoginFragment.Content also had an onClickPlayer lambda,
    // but in the Compose Navigation setup, navigation is handled directly by navController.
    // So, onClickPlayer is passed down to ReadyScreen which then uses navController.

    val pullRefreshState =
        rememberPullRefreshState(refreshing = state is LoginViewState.Loading, onRefresh = {
            state.onRefresh() // Assuming onRefresh is part of the state and takes context
        })

    LaunchedEffect(Unit) {
        loginViewModel.loadPlayerData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(resId = R.drawable.bg_login)

        when (val viewState = state) { // Use a stable variable for the when expression
            is LoginViewState.Loading -> LoadingScreen(R.drawable.loading)
            is LoginViewState.Success -> ReadyScreen(
                navController = navController,
                mainActivityViewModel = mainActivityViewModel, // Pass it down
                players = viewState.players,
                onDeletePlayer = viewState.onDeletePlayer, // This comes from LoginViewModel's state
                onAddPlayer = viewState.onAddPlayer     // This comes from LoginViewModel's state
            )

            is LoginViewState.CouldNotFindPlayerError -> CouldNotFindPlayerErrorScreen(
                player = viewState.player,
                onAddPlayer = viewState.onAddPlayer,
                onClickBack = viewState.onClickBack
            )
        }
        SplinterPullRefreshIndicator(pullRefreshState) // Pass refreshing state to indicator
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
    navController: NavHostController,
    mainActivityViewModel: MainActivityViewModel, // Added mainActivityViewModel
    players: List<LoginViewModel.PlayerRowInfo>,
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
                navController = navController,
                mainActivityViewModel = mainActivityViewModel, // Pass it down
                players = players,
                onDelete = onDeletePlayer
            )
        }
        Spacer(Modifier.height(12.dp))
        AddAccountCard(onAddPlayer, "") // onAddPlayer here is loginViewModel.onAddPlayer via state
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
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        tint = Color.White
                    ) // Added contentDescription
                }
            }
        }
    }
}

@Composable
fun AccountsList(
    navController: NavHostController,
    mainActivityViewModel: MainActivityViewModel, // Added mainActivityViewModel
    players: List<LoginViewModel.PlayerRowInfo>,
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
            val textMinWidth = remember { mutableStateOf(0.dp) }
            players.forEach {
                PlayerItem(
                    navController = navController,
                    mainActivityViewModel = mainActivityViewModel, // Pass it down
                    player = it,
                    minTextWidth = textMinWidth,
                    onDelete = onDelete
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerItem(
    navController: NavHostController,
    mainActivityViewModel: MainActivityViewModel, // Added mainActivityViewModel
    player: LoginViewModel.PlayerRowInfo,
    minTextWidth: MutableState<Dp>,
    onDelete: (player: String) -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier
            .clickable {
                mainActivityViewModel.setPlayer(player.name) // Set player in session via MainActivityViewModel
                navController.navigate("account_details/${player.name}")
            },
        text = {
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
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                ) // Added contentDescription
            }
        }
    )

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Remove account") },
            text = { Text("Are you sure you want to remove ${player.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(player.name)
                    showDeleteDialog.value = false
                }) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
@Preview
fun PlayerRowPreview() {
    val minWidth = remember { mutableStateOf(10.dp) }
    // PlayerItem(navController = rememberNavController(), player = LoginViewModel.PlayerRowInfo("splinteraccount"), minTextWidth = minWidth, onDelete = {})
    // Previewing composables that require NavController can be tricky.
    // For now, focusing on the structural recreation.
}
