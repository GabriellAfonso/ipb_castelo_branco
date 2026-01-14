package com.gabrielafonso.ipb.castelobranco.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    appName: String,
    logo: Painter,
    accountImage: Painter,
    onMenuClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF045A48),
            titleContentColor = Color.White
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = appName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        navigationIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
                Spacer(modifier = Modifier.width(25.dp)) // distância do menu para o logo
                Image(
                    painter = logo,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(24.dp)                   // altura fixa
                        .aspectRatio(451 / 392.toFloat()) // largura acompanha proporção
                )
            }
        },
        actions = {
            IconButton(onClick = onAccountClick) {
                Image(
                    painter = accountImage,
                    contentDescription = "Conta",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    )
}
