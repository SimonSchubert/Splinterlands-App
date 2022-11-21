package com.splintergod.app.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun BoxScope.BackgroundImage(resId: Int) {
    Image(
        painterResource(id = resId),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier.matchParentSize()
    )
}