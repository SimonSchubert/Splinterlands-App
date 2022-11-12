package com.example.splinterlandstest.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.splinterlandstest.Cache
import com.example.splinterlandstest.MainActivityViewModel
import com.example.splinterlandstest.R
import com.example.splinterlandstest.Requests
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.android.ext.android.get

/**
 * Collection fragment
 */
class CollectionFragment : Fragment() {

    val cache: Cache = get()
    val requests: Requests = get()

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel by viewModels<CollectionFragmentViewModel> {
        CollectionViewModelFactory(activityViewModel.playerName, cache, requests)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.collection)

        return ComposeView(requireContext()).apply {
            setContent {
                Content(viewModel.state.collectAsState().value)
            }
        }
    }
}

@Composable
fun Content(state: CollectionViewState) {
    val swipeRefreshState = rememberSwipeRefreshState(false)

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
        SwipeRefresh(
            state = swipeRefreshState,
            swipeEnabled = state !is CollectionViewState.Loading,
            onRefresh = {
                state.onRefresh()
            },
        ) {
            when (state) {
                is CollectionViewState.Loading -> LoadingScreen()
                is CollectionViewState.Success -> ReadyScreen(
                    cards = state.cards,
                    filterRarityStates = state.filterRarityStates,
                    onClickRarity = state.onClickRarity,
                    filterEditionStates = state.filterEditionStates,
                    onClickEdition = state.onClickEdition,
                    filterElementStates = state.filterElementStates,
                    onClickElement = state.onClickElement
                )
                is CollectionViewState.Error -> ErrorScreen()
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun ReadyScreen(
    cards: List<CardViewState>,
    filterRarityStates: List<FilterRarityState>,
    onClickRarity: (Int) -> Unit,
    filterEditionStates: List<FilterEditionState>,
    filterElementStates: List<FilterElementState>,
    onClickElement: (String) -> Unit,
    onClickEdition: (Int) -> Unit
) {
    val showFilterDialog = remember { mutableStateOf(false) }

    Scaffold(
        content = { paddingValues ->
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(minSize = 96.dp)
            ) {
                items(cards.size) { index ->
                    CardItem(cards[index])
                }
            }
            if (showFilterDialog.value) {
                FilterDialog(
                    filterRarityStates,
                    onClickRarity,
                    filterEditionStates,
                    onClickEdition,
                    filterElementStates,
                    onClickElement,
                    onDismiss = {
                        showFilterDialog.value = false
                    })
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showFilterDialog.value = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.tune), contentDescription = null
                )
            }
        },
        backgroundColor = Color.Black
    )
}

@Composable
fun FilterDialog(
    filterRarityStates: List<FilterRarityState>,
    onClickRarity: (Int) -> Unit,
    filterEditionStates: List<FilterEditionState>,
    onClickEdition: (Int) -> Unit,
    filterElementStates: List<FilterElementState>,
    onClickElement: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(backgroundColor = Color.Black) {
            Column(modifier = Modifier.padding(4.dp)) {

                Text(
                    text = "Rarity",
                    color = Color.White,
                    style = MaterialTheme.typography.h5
                )
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    filterRarityStates.forEach { rarityState ->
                        Box(
                            modifier = Modifier
                                .filterButtonModifier(rarityState.selected, rarityState.color)
                                .clickable { onClickRarity(rarityState.id) }
                        )
                    }
                }

                Text(
                    text = "Editions",
                    color = Color.White,
                    style = MaterialTheme.typography.h5
                )
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    filterEditionStates.forEach { editionState ->
                        Box(
                            modifier = Modifier
                                .filterButtonModifier(editionState.selected, Color.Black)
                                .clickable { onClickEdition(editionState.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = editionState.imageRes),
                                modifier = Modifier.height(30.dp),
                                contentDescription = null
                            )
                        }
                    }
                }

                Text(
                    text = "Elements",
                    color = Color.White,
                    style = MaterialTheme.typography.h5
                )
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    filterElementStates.forEach { elementState ->
                        Box(
                            modifier = Modifier
                                .filterButtonModifier(elementState.selected, Color.Black)
                                .clickable { onClickElement(elementState.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = elementState.imageRes),
                                modifier = Modifier.height(30.dp),
                                contentDescription = null
                            )
                        }
                    }
                }

            }
        }
    }
}


fun Modifier.filterButtonModifier(
    selected: Boolean,
    background: Color
): Modifier {

    return this
        .size(50.dp)
        .clip(CircleShape)
        .border(
            4.dp, if (selected) {
                Color(0XFFFFA827)
            } else {
                Color.Gray
            }, CircleShape
        )
        .background(background)
}

@Composable
fun CardItem(card: CardViewState) {
    Column {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(card.imageUrl)
                .build(),
            placeholder = painterResource(card.placeHolderRes),
            contentDescription = null
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