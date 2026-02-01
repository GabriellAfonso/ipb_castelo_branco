package com.gabrielafonso.ipb.castelobranco.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val darkModeFlow: Flow<Boolean?>
    suspend fun setDarkMode(value: Boolean)
}
