package com.gabrielafonso.ipb.castelobranco.features.worshiphub.register.domain.repository

import com.gabrielafonso.ipb.castelobranco.features.worshiphub.tables.model.SundayPlayPushItem

interface WorshipRegisterRepository {
    suspend fun pushSundayPlays(
        date: String,
        plays: List<SundayPlayPushItem>
    ): Result<Unit>
}