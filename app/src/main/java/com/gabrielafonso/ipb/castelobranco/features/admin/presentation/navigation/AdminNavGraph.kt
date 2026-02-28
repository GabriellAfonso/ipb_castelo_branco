package com.gabrielafonso.ipb.castelobranco.features.admin.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gabrielafonso.ipb.castelobranco.features.admin.presentation.views.AdminView
import com.gabrielafonso.ipb.castelobranco.features.admin.register.presentation.views.MusicRegistrationView
import com.gabrielafonso.ipb.castelobranco.features.admin.schedule.presentation.views.AdminScheduleView

@Stable
data class AdminNav(
    val back: () -> Unit,
    val register: () -> Unit,
    val schedule: () -> Unit,
)

object AdminRoutes {
    const val ADMIN = "AdminMain"
    const val REGISTER = "AdminRegister"
    const val SCHEDULE = "AdminSchedule"
}

@Composable
fun AdminNavGraph(
    navController: NavHostController,
    onFinish: () -> Unit,
) {
    fun popOrFinish() {
        if (!navController.popBackStack()) onFinish()
    }

    val nav = AdminNav(
        back = { popOrFinish() },
        register = { navController.navigate(AdminRoutes.REGISTER) },
        schedule = { navController.navigate(AdminRoutes.SCHEDULE) },
    )

    NavHost(navController = navController, startDestination = AdminRoutes.ADMIN) {

        composable(AdminRoutes.ADMIN) {
            AdminView(nav = nav)
        }

        composable(AdminRoutes.REGISTER) {
            MusicRegistrationView(nav)
        }

        composable(AdminRoutes.SCHEDULE) {
            AdminScheduleView(
                nav = nav,
                onShare = { text ->
                    // mesmo Intent que você já usa no schedule normal
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                    }
                    navController.context.startActivity(
                        android.content.Intent.createChooser(intent, "Compartilhar escala")
                    )
                }
            )
        }
    }
}
