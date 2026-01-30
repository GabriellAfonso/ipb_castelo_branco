package com.gabrielafonso.ipb.castelobranco.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.content.Context
import android.content.Intent

import com.gabrielafonso.ipb.castelobranco.R
import com.gabrielafonso.ipb.castelobranco.ui.components.Highlight
import com.gabrielafonso.ipb.castelobranco.ui.components.CustomButton
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseScreen
import com.gabrielafonso.ipb.castelobranco.ui.screens.hymnal.HymnalActivity
import com.gabrielafonso.ipb.castelobranco.ui.screens.monthschedule.MonthScheduleActivity
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.WorshipHubActivity

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val actions = remember(context) { MainActions(context) }

    BaseScreen(tabName = "IPB Castelo Branco") { innerPadding ->
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

            ButtonGrid(actions = actions)
        }
    }
}

class MainActions(private val context: Context) {

    fun openWorshipHub() = openActivity(WorshipHubActivity::class.java)
    fun openSchedule() = openActivity(MonthScheduleActivity::class.java)
    fun openHymnal() = openActivity(HymnalActivity::class.java)

    private fun <T> openActivity(activity: Class<T>) {
        context.startActivity(Intent(context, activity))
    }
}

/**
 * Modelo puro (sem Android)
 */
data class ButtonInfo(
    val iconRes: Int,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ButtonGrid(actions: MainActions) {
    val iconColor = MaterialTheme.colorScheme.primaryContainer

    val buttons = remember {
        listOf(
            ButtonInfo(R.drawable.louvor_icon, "Louvor", iconColor, actions::openWorshipHub),
            ButtonInfo(R.drawable.calendar_icon, "Escala", iconColor, actions::openSchedule),
            ButtonInfo(R.drawable.gallery_icon, "Galeria", iconColor) { println("Galeria clicked") },
            ButtonInfo(R.drawable.sarca_ipb, "Hinário", iconColor, actions::openHymnal),
            ButtonInfo(R.drawable.sarca_ipb, "Exemplo", iconColor) { println("Sample 1 clicked") },
            ButtonInfo(R.drawable.sarca_ipb, "Exemplo", iconColor) { println("Sample 2 clicked") }
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ButtonRow(buttons.subList(0, 3))
        ButtonRow(buttons.subList(3, 6))
    }
}

@Composable
private fun ButtonRow(rowButtons: List<ButtonInfo>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(25.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        rowButtons.forEach { button ->
            CustomButton(
                image = painterResource(id = button.iconRes),
                text = button.label,
                backgroundColor = button.color,
                onClick = button.onClick
            )
        }
    }
}
