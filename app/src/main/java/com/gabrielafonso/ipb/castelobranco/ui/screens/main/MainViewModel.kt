package com.gabrielafonso.ipb.castelobranco.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielafonso.ipb.castelobranco.data.repository.HymnalRepositoryImpl
import com.gabrielafonso.ipb.castelobranco.data.repository.MonthScheduleRepositoryImpl
import com.gabrielafonso.ipb.castelobranco.data.repository.SongsRepositoryImpl
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SongsRepositoryImpl,
    private val hymnalRepository: HymnalRepositoryImpl,
    private val monthScheduleRepository: MonthScheduleRepositoryImpl
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        preload()
    }

    private fun preload() {
        viewModelScope.launch {
            val okSongsBySunday = repository.refreshSongsBySunday()
            val okTopSongs = repository.refreshTopSongs()
            val okTopTones = repository.refreshTopTones()
            val okSuggestedSongs = repository.refreshSuggestedSongs()
            val okHymnal = hymnalRepository.refreshHymnal()
            val okMonthSchedule = monthScheduleRepository.refreshMonthSchedule()


            if (!okSongsBySunday) Log.w(TAG, "refreshSongsBySunday falhou")
            else Log.d(TAG, "refreshSongsBySunday sucesso")

            if (!okTopSongs) Log.w(TAG, "refreshTopSongs falhou")
            else Log.d(TAG, "refreshTopSongs sucesso")

            if (!okTopTones) Log.w(TAG, "refreshTopTones falhou")
            else Log.d(TAG, "refreshTopTones sucesso")

            if (!okSuggestedSongs) Log.w(TAG, "refreshSuggestedSongs falhou")
            else Log.d(TAG, "refreshSuggestedSongs sucesso")

            if (!okHymnal) Log.w(TAG, "refreshHymnal falhou")
            else Log.d(TAG, "refreshHymnal sucesso")

            if (!okMonthSchedule) Log.w(TAG, "refreshMonthSchedule falhou")
            else Log.d(TAG, "refreshMonthSchedule sucesso")

        }
    }
}
