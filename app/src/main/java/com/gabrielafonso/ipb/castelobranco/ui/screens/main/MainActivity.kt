package com.gabrielafonso.ipb.castelobranco.ui.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.gabrielafonso.ipb.castelobranco.MyApp

import com.gabrielafonso.ipb.castelobranco.ui.theme.IPBCasteloBrancoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as MyApp
        val viewModel = MainViewModel(
            app.appContainer.songsRepository,
            app.appContainer.hymnalRepository,
            app.appContainer.monthScheduleRepository
        )
        enableEdgeToEdge()
        setContent {
            IPBCasteloBrancoTheme {
                MainScreen()
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    IPBCasteloBrancoTheme {
        MainScreen()
    }
}
