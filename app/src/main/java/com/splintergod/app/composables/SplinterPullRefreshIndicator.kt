@file:OptIn(ExperimentalMaterialApi::class)

package com.splintergod.app.composables

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.splintergod.app.R

@Composable
fun BoxScope.SplinterPullRefreshIndicator(pullRefreshState: PullRefreshState, hide: Boolean = false) {

    AsyncImage(
        modifier = Modifier
            .width(50.dp)
            .align(Alignment.TopCenter)
            .pullRefreshIndicatorTransform(pullRefreshState),
        model = R.drawable.loading,
        contentDescription = "LOADING"
    )
}