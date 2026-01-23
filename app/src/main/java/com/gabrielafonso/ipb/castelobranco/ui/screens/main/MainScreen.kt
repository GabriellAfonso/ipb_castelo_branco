package com.gabrielafonso.ipb.castelobranco.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

//import com.gabrielafonso.ipb.castelobranco.Greeting
import com.gabrielafonso.ipb.castelobranco.R

import com.gabrielafonso.ipb.castelobranco.ui.components.Highlight
import com.gabrielafonso.ipb.castelobranco.ui.components.ButtonGrid
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseScreen


@Composable
fun MainScreen() {
    BaseScreen(
        tabName = "IPB Castelo Branco",
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Highlight()
            Spacer(modifier = Modifier.height(60.dp))
            ButtonGrid()
        }
    }
}
