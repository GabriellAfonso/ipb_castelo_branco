package com.gabrielafonso.ipb.castelobranco.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gabrielafonso.ipb.castelobranco.R

data class ButtonInfo(
    val drawable: Int,
    val label: String,
    val color: Color
)
@Composable
fun ButtonGrid() {
    val iconColor = Color(0xFF157C53)
    val buttons = listOf(
        ButtonInfo(R.drawable.louvor_icon, "Louvor", iconColor),
        ButtonInfo(R.drawable.calendar_icon, "Escala", iconColor),
        ButtonInfo(R.drawable.gallery_icon, "Galeria", iconColor),
        ButtonInfo(R.drawable.teste_logo, "Hinário", iconColor),
        ButtonInfo(R.drawable.teste_logo, "Sample", iconColor),
        ButtonInfo(R.drawable.teste_logo, "Sample", iconColor)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Primeira linha
        Row(horizontalArrangement = Arrangement.spacedBy(25.dp), modifier = Modifier.padding(vertical = 16.dp)) {
            buttons.take(3).forEachIndexed { index, (drawable, label, color) ->
                CustomButton(
                    image = painterResource(id = drawable),
                    text = label,
                    backgroundColor = color,
                    onClick = { println("Button $index clicked") }
                )
            }
        }

        // Segunda linha
        Row(horizontalArrangement = Arrangement.spacedBy(25.dp), modifier = Modifier.padding(vertical = 16.dp)) {
            buttons.drop(3).take(3).forEachIndexed { index, (drawable, label, color) ->
                CustomButton(
                    image = painterResource(id = drawable),
                    text = label,
                    backgroundColor = color,
                    onClick = { println("Button ${index + 3} clicked") }
                )
            }
        }
    }
}