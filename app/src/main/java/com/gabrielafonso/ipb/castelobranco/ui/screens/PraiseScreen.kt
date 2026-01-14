package com.gabrielafonso.ipb.castelobranco.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gabrielafonso.ipb.castelobranco.R
import com.gabrielafonso.ipb.castelobranco.data.lastSongs
import com.gabrielafonso.ipb.castelobranco.data.model.TableView
import com.gabrielafonso.ipb.castelobranco.data.suggestedSongs
import com.gabrielafonso.ipb.castelobranco.data.topSongs
import com.gabrielafonso.ipb.castelobranco.data.topTones
import com.gabrielafonso.ipb.castelobranco.ui.components.PraiseTables
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember



data class Praise(
    val title: String,
    val singer: String
)

@Composable
fun PraiseScreen(onBack: () -> Unit) {

    var currentView by remember { mutableStateOf(TableView.LAST_SONGS) }

    val data = when (currentView) {
        TableView.LAST_SONGS -> lastSongs
        TableView.TOP_SONGS -> topSongs
        TableView.TOP_TONES -> topTones
        TableView.SUGGESTED_SONGS -> suggestedSongs

    }

    BaseScreen(
        tabName = "Louvores",
        logo = painterResource(id = R.drawable.louvor_icon),
        accountImage = painterResource(id = R.drawable.ic_account),
        showBackArrow = true,
        onBackClick = onBack,
        backgroundColor = Color(0xFFc7dbd2)
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(0.dp)
        ) {

            PraiseTables(
                currentView = currentView,
                onViewChange = { currentView = it },
                data = data,
            )

        }
    }
}
