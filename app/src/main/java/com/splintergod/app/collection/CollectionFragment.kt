@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.splintergod.app.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.splintergod.app.MainActivity
import com.splintergod.app.R
import com.splintergod.app.carddetail.CardDetailFragment
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
                Content(viewModel.state.collectAsState().value) {
                    viewModel.session.currentCardDetailId = it.cardId
                    viewModel.session.currentCardDetailLevel = it.level
                    (requireActivity() as MainActivity).setCurrentFragment(CardDetailFragment())
                }
            }
        }
    }
}

@Composable
fun Content(state: CollectionViewState, onClickCard: (id: CardViewState) -> Unit) {

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
                filterRoleStates = state.filterRoleStates,
                onClickRole = state.onClickRole,
                sortingStates = state.sortingElementStates,
                selectedSorting = state.selectedSorting,
                onClickSorting = state.onClickSorting,
                onClickCard = onClickCard
            )

            is CollectionViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState)
    }
}

@Composable
fun ReadyScreen(
    cards: List<CardViewState>,
    filterRarityStates: List<FilterState.Rarity>,
    onClickRarity: (String) -> Unit,
    filterEditionStates: List<FilterState.Edition>,
    onClickEdition: (String) -> Unit,
    filterElementStates: List<FilterState.Basic>,
    onClickElement: (String) -> Unit,
    filterFoilStates: List<FilterState.Basic>,
    onClickFoil: (String) -> Unit,
    filterRoleStates: List<FilterState.Basic>,
    onClickRole: (String) -> Unit,
    sortingStates: List<SortingState>,
    onClickSorting: (CollectionViewModel.Sorting) -> Unit,
    selectedSorting: SortingState?,
    onClickCard: (id: CardViewState) -> Unit
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
                    CardItem(cards[index], onClickCard)
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
                    filterRoleStates,
                    onClickRole,
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
    filterRarityStates: List<FilterState.Rarity>,
    onClickRarity: (String) -> Unit,
    filterEditionStates: List<FilterState.Edition>,
    onClickEdition: (String) -> Unit,
    filterElementStates: List<FilterState.Basic>,
    onClickElement: (String) -> Unit,
    filterFoilStates: List<FilterState.Basic>,
    onClickFoil: (String) -> Unit,
    filterRoleStates: List<FilterState.Basic>,
    onClickRole: (String) -> Unit,
    sortingStates: List<SortingState>,
    onClickSorting: (CollectionViewModel.Sorting) -> Unit,
    selectedSorting: SortingState?,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(backgroundColor = Color.Black.copy(alpha = 0.7f)) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(12.dp)
            ) {


                Box {
                    val mExpanded = remember { mutableStateOf(false) }

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

                FilterGroup("Rarity",
                    content = {
                        filterRarityStates.forEach { rarityState ->
                            Box(
                                modifier = Modifier
                                    .filterButtonModifier(rarityState.selected, rarityState.color)
                                    .clickable { onClickRarity(rarityState.id) }
                            )
                        }
                    })

                Spacer(Modifier.height(6.dp))

                FilterGroup("Editions",
                    content = {
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
                    })

                Spacer(Modifier.height(6.dp))

                BasicFilterGroup("Elements", filterElementStates, onClickElement)

                Spacer(Modifier.height(6.dp))

                Row {

                    Column {
                        BasicFilterGroup("Foil", filterFoilStates, onClickFoil)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        BasicFilterGroup("Role", filterRoleStates, onClickRole)
                    }
                }
            }
        }
    }
}

@Composable
fun BasicFilterGroup(
    title: String,
    filterStates: List<FilterState.Basic>,
    onClick: (String) -> Unit
) {
    FilterGroup(title,
        content = {
            filterStates.forEach { roleState ->
                Box(
                    modifier = Modifier
                        .filterButtonModifier(roleState.selected, Color.Black)
                        .clickable { onClick(roleState.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = roleState.imageRes),
                        modifier = Modifier.height(30.dp),
                        contentDescription = null
                    )
                }
            }
        })
}

@Composable
fun FilterGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.h6
    )
    FlowRow(
        mainAxisSpacing = 4.dp,
        crossAxisSpacing = 4.dp
    ) {
        content()
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
fun CardItem(card: CardViewState, onClickCard: (id: CardViewState) -> Unit) {
    Box(Modifier.clickable {
        onClickCard(card)
    }) {
        AsyncImage(
            model = card.imageUrl,
            placeholder = painterResource(card.placeHolderRes),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )
        if (card.quantity > 1) {
            val bannerYOffset = if (card.cardId.toInt() > 300) {
                5.dp
            } else {
                1.dp
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = bannerYOffset),
                contentAlignment = Alignment.TopCenter
            ) {

                Image(
                    painter = painterResource(id = R.drawable.quantity_banner),
                    modifier = Modifier.width(30.dp),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.offset(y = 2.dp),
                    text = card.quantity.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }
}