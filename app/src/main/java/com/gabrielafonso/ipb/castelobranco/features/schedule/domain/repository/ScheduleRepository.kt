package com.gabrielafonso.ipb.castelobranco.features.schedule.domain.repository

import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.RefreshResult
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.SnapshotState
import com.gabrielafonso.ipb.castelobranco.features.schedule.domain.model.MonthSchedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun observeMonthSchedule(): Flow<SnapshotState<MonthSchedule>>
    suspend fun refreshMonthSchedule(): RefreshResult
}