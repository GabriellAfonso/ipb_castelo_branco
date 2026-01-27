package com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.views.WorshipHubView

object WorshipHubRoutes {
    const val Hub = "worship_hub"
    const val Tables = "tables"
    const val Other = "other"
}

@Composable
fun WorshipHubNavGraph(
    navController: NavHostController,
    viewModel: WorshipHubViewModel
) {
    NavHost(navController = navController, startDestination = WorshipHubRoutes.Hub) {
        composable(WorshipHubRoutes.Hub) {
            WorshipHubView(
                onTablesClick = { navController.navigate(WorshipHubRoutes.Tables) },
            )
        }
        composable(WorshipHubRoutes.Tables) {
            WorshipSongsTableScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
