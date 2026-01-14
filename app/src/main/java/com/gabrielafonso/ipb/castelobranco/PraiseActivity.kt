package com.gabrielafonso.ipb.castelobranco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gabrielafonso.ipb.castelobranco.ui.screens.PraiseScreen

import com.gabrielafonso.ipb.castelobranco.ui.theme.IPBCasteloBrancoTheme

class PraiseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IPBCasteloBrancoTheme {

                PraiseScreen(
                    onBack = { finish() } // fecha a Activity ao clicar na seta
                )

                }
            }
        }
    }


@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IPBCasteloBrancoTheme {
        Greeting2("Android")
    }
}