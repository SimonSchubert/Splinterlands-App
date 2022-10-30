package com.example.splinterlandstest.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R

/**
 * Collection fragment
 */
class LoginFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = "Login"

        return ComposeView(requireContext()).apply {
            setContent {
                Login(
                    onClick = { player ->
                        activityViewModel.setPlayer(requireContext(), player)
                    },
                    onDelete = { player ->
                        activityViewModel.deletePlayer(requireContext(), player)
                    })
            }
        }
    }
}

@Composable
fun Login(
    onClick: (player: String) -> Unit,
    onDelete: (player: String) -> Unit,
    viewModel: LoginFragmentViewModel = viewModel(factory = LoginFragmentViewModelFactory(LocalContext.current))
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painterResource(id = R.drawable.bg_login),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .align(alignment = Alignment.Center)
        )


        val scrollState = rememberScrollState()

        Column(
            Modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painterResource(id = R.drawable.splinterlands_logo),
                contentDescription = ""
            )


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

                    viewModel.players.forEach {
                        PlayerItem(it, textMinWidth, onClick) { player ->
                            onDelete(player)
                            viewModel.onDelete(player)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

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
                        var text by remember { mutableStateOf(TextFieldValue("")) }
                        TextField(value = text, textStyle = TextStyle.Default.copy(color = Color.White), onValueChange = {
                            text = it
                        },
                            placeholder = {
                                Text(
                                    "Player",
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            }
                        )

                        IconButton(onClick = {
                            viewModel.onAdd(text.text)
                        }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerItem(
    player: LoginFragmentViewModel.PlayerRowInfo,
    minTextWidth: MutableState<Dp>,
    onClick: (player: String) -> Unit,
    onDelete: (player: String) -> Unit
) {
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

                val image: Painter = rememberAsyncImagePainter(player.chestUrl)
                Image(
                    painter = image,
                    modifier = Modifier.size(40.dp, 40.dp),
                    contentDescription = ""
                )

                Text(text = player.timeLeft, color = Color.White)
            }
        }
    },
        trailing = {
            IconButton(onClick = { onDelete(player.name) }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = Color.White)
            }
        })
}