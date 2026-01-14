package com.gabrielafonso.ipb.castelobranco.ui.tables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RefreshButton() {
    Button(
        onClick = { /* chamada HTTP futura */ },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Atualizar Sugestões")
    }
}
