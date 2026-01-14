package com.gabrielafonso.ipb.castelobranco.ui.screens


import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter


import com.gabrielafonso.ipb.castelobranco.ui.components.TopBar

@Composable
fun BaseScreen(
    tabName: String,
    logo: Painter,
    accountImage: Painter,
    showBackArrow: Boolean = false,
    onMenuClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    backgroundColor: Color = Color.White,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = backgroundColor,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                tabName = tabName,
                logo = logo,
                accountImage = accountImage,
                showBackArrow = showBackArrow,
                onMenuClick = onMenuClick,
                onBackClick = onBackClick,
                onAccountClick = onAccountClick
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
