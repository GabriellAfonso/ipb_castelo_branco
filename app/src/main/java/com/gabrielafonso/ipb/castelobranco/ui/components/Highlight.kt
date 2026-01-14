package com.gabrielafonso.ipb.castelobranco.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


@Composable
fun Highlight() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(250.dp)
            .background(Color(0xFF8000FF)), // purple
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "DESTAQUE", // placeholder
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}