package com.splintergod.app.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ErrorScreen(message: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Optional: add some padding
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: "An error occurred. Please try again.", // Display message or default
            color = Color.White,
            textAlign = TextAlign.Center // Optional: for better display of multi-line messages
        )
    }
}