package com.gabrielafonso.ipb.castelobranco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabrielafonso.ipb.castelobranco.ui.screens.MainScreen
import com.gabrielafonso.ipb.castelobranco.ui.theme.IPBCasteloBrancoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IPBCasteloBrancoTheme {
                MainScreen()
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun BotaoCustomizado(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Ícone do botão",
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    IPBCasteloBrancoTheme {
        MainScreen()
    }
}
