// app/src/main/java/com/gabrielafonso/ipb/castelobranco/ui/screens/base/BaseViewModelProvider.kt
package com.gabrielafonso.ipb.castelobranco.ui.screens.base

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gabrielafonso.ipb.castelobranco.MyApp
import com.gabrielafonso.ipb.castelobranco.ui.screens.hymnal.HymnalViewModel
import com.gabrielafonso.ipb.castelobranco.ui.screens.monthschedule.MonthScheduleViewModel
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.WorshipHubViewModel

object BaseViewModelProvider {

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApp

            WorshipHubViewModel(
                repository = app.appContainer.songsRepository
            )
        }

        initializer {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApp

            HymnalViewModel(
                repository = app.appContainer.hymnalRepository
            )
        }

        initializer {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApp

            MonthScheduleViewModel(
                repository = app.appContainer.monthScheduleRepository
            )
        }
    }
}
