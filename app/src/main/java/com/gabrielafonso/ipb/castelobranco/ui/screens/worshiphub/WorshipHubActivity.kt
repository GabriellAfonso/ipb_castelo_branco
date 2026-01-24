package com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySetItem
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseActivity
import com.gabrielafonso.ipb.castelobranco.ui.screens.base.BaseViewModelProvider
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.views.WorshipHubScreen
import com.gabrielafonso.ipb.castelobranco.ui.theme.IPBCasteloBrancoTheme

class WorshipHubActivity : BaseActivity() {

    private val viewModel: WorshipHubViewModel by viewModels { BaseViewModelProvider.Factory }

    override fun onPreCreate(savedInstanceState: Bundle?) {
        super.onPreCreate(savedInstanceState)
        // Inicialize ViewModel, leia extras da intent, registre receivers, etc.
        // ex: val id = intent?.getStringExtra("EXTRA_ID")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Ações que precisam ocorrer após setContent (não dentro do Composable)
    }

    @Composable
    override fun ScreenContent() {
        val navController = rememberNavController()
        WorshipHubNavGraph(
            navController = navController,
            viewModel = viewModel
        )
//        WorshipHubScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWorshipHub() {
    IPBCasteloBrancoTheme {
        WorshipHubScreen(onTablesClick = {})
    }
}


@Preview(name = "SongsTable", showBackground = true)
@Composable
fun PreviewSongsTable() {
    val fakeSundays = listOf(
        SundaySet(
            date = "2026-01-21",
            songs = listOf(
                SundaySetItem(position = 1, title = "Música 1", tone = "C", artist = "Artista A"),
                SundaySetItem(position = 2, title = "Música 2", tone = "G", artist = "Artista B")
            )
        )
    )

    IPBCasteloBrancoTheme {
        WorshipSongsTableUi(
            onBack = {},
            sundays = fakeSundays,
            topSongs = emptyList(),
            topTones = emptyList()

        )
    }
}
// Exemplo para outras screens:
// @Preview(name = "SongsTable", showBackground = true)
// @Composable
// fun PreviewSongsTable() {
//     IPBCasteloBrancoTheme {
//         SongsTableScreen(onBack = {})
//     }
// }

