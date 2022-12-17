@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.splintergod.app.collection

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.splintergod.app.R
import com.splintergod.app.composables.BackgroundImage
import com.splintergod.app.composables.ErrorScreen
import com.splintergod.app.composables.LoadingScreen
import com.splintergod.app.composables.SplinterPullRefreshIndicator
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Collection fragment
 */
class CollectionFragment : Fragment() {

    private val viewModel: CollectionViewModel by viewModel()

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

    val pullRefreshState = rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = { state.onRefresh() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(resId = R.drawable.bg_mountain)

        when (state) {
            is CollectionViewState.Loading -> LoadingScreen(R.drawable.collection)
            is CollectionViewState.Success -> ReadyScreen(
                cards = state.cards,
                filterRarityStates = state.filterRarityStates,
                onClickRarity = state.onClickRarity,
                filterEditionStates = state.filterEditionStates,
                onClickEdition = state.onClickEdition,
                filterElementStates = state.filterElementStates,
                onClickElement = state.onClickElement,
                filterFoilStates = state.filterFoilStates,
                onClickFoil = state.onClickFoil,
                sortingStates = state.sortingElementStates,
                selectedSorting = state.selectedSorting,
                onClickSorting = state.onClickSorting
            )
            is CollectionViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun ReadyScreen(
    cards: List<CardViewState>,
    filterRarityStates: List<FilterRarityState>,
    onClickRarity: (Int) -> Unit,
    filterEditionStates: List<FilterEditionState>,
    onClickEdition: (Int) -> Unit,
    filterElementStates: List<FilterElementState>,
    onClickElement: (String) -> Unit,
    filterFoilStates: List<FilterFoilState>,
    onClickFoil: (String) -> Unit,
    sortingStates: List<SortingState>,
    onClickSorting: (CollectionViewModel.Sorting) -> Unit,
    selectedSorting: SortingState?
) {
    val showFilterDialog = remember { mutableStateOf(false) }

    Scaffold(
        content = { paddingValues ->
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth(),
                columns = GridCells.Adaptive(minSize = 112.dp)
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
                    filterFoilStates,
                    onClickFoil,
                    sortingStates,
                    onClickSorting,
                    selectedSorting,
                    onDismiss = {
                        showFilterDialog.value = false
                    })
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                backgroundColor = Color(0XFFff9300),
                onClick = {
                    showFilterDialog.value = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.tune),
                    contentDescription = "FAB"
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
    filterFoilStates: List<FilterFoilState>,
    onClickFoil: (String) -> Unit,
    sortingStates: List<SortingState>,
    onClickSorting: (CollectionViewModel.Sorting) -> Unit,
    selectedSorting: SortingState?,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(backgroundColor = Color.Black) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(12.dp)
            ) {

                val mExpanded = remember { mutableStateOf(false) }

                Box {
                    Row(modifier = Modifier.clickable { mExpanded.value = !mExpanded.value }) {
                        Text(
                            text = "Sort by: ${selectedSorting?.name}",
                            color = Color.White,
                            style = MaterialTheme.typography.h6
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(expanded = mExpanded.value,
                        onDismissRequest = { mExpanded.value = false }) {

                        sortingStates.forEach { sorting ->
                            DropdownMenuItem(onClick = {
                                mExpanded.value = false
                                onClickSorting(sorting.id)
                            }) {
                                Text(
                                    text = sorting.name,
                                    color = if (sorting.selected) {
                                        Color(0XFFff9300)
                                    } else {
                                        Color.Black
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Rarity",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
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

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Editions",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
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

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Elements",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
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

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Foil",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
                )
                FlowRow(
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 4.dp
                ) {
                    filterFoilStates.forEach { foilState ->
                        Box(
                            modifier = Modifier
                                .filterButtonModifier(foilState.selected, Color.Black)
                                .clickable { onClickFoil(foilState.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = foilState.imageRes),
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
            model = card.imageUrl,
            placeholder = painterResource(card.placeHolderRes),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}