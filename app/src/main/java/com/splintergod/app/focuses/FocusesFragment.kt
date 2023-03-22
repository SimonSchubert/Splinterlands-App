@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.focuses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.splintergod.app.models.Focus
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Focuses fragment
 */
class FocusesFragment : Fragment() {

    private val viewModel: FocusesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        activity?.title = getString(R.string.focuses)

        return ComposeView(requireContext()).apply {
            setContent {
                Content(viewModel.state.collectAsState().value)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadRewards()
    }
}

@Composable
fun Content(state: FocusesViewState) {

    val pullRefreshState = rememberPullRefreshState(refreshing = state.isRefreshing, onRefresh = { state.onRefresh() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(resId = R.drawable.bg_gate)

        when (state) {
            is FocusesViewState.Loading -> LoadingScreen(R.drawable.faq)
            is FocusesViewState.Success -> ReadyScreen(focuses = state.focuses)
            is FocusesViewState.Error -> ErrorScreen()
        }

        SplinterPullRefreshIndicator(pullRefreshState, state.isRefreshing)
    }
}

@Composable
fun ReadyScreen(
    focuses: List<Focus>
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 300.dp)
    ) {
        items(focuses.size) { index ->
            FocusItem(focuses[index])
        }
    }
}

@Composable
fun FocusItem(focus: Focus) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = focus.name.replace("_", " ").uppercase(),
                color = Color(0XFFffa500),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            focus.data.getSplinterDrawable()?.let { splinterDrawable ->
                Image(
                    painter = painterResource(id = splinterDrawable),
                    modifier = Modifier.size(24.dp, 24.dp),
                    contentDescription = ""
                )
            }

            focus.data.getAbilityUrls().forEach { url ->
                AsyncImage(
                    model = url,
                    modifier = Modifier.size(32.dp, 32.dp),
                    contentDescription = ""
                )
            }
        }

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = focus.data.description,
            color = Color.White
        )
    }
}

@Composable
@Preview
fun RewardsPreview() {
    val mockFocuses = emptyList<Focus>()
    ReadyScreen(focuses = mockFocuses)
}