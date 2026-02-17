package com.gabrielafonso.ipb.castelobranco.features.schedule.data.repository

import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.RefreshResult
import com.gabrielafonso.ipb.castelobranco.core.domain.snapshot.SnapshotState

import com.gabrielafonso.ipb.castelobranco.features.schedule.data.snapshot.MonthScheduleSnapshotRepository
import com.gabrielafonso.ipb.castelobranco.features.schedule.domain.model.MonthSchedule
import com.gabrielafonso.ipb.castelobranco.features.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val snapshot: MonthScheduleSnapshotRepository
) : ScheduleRepository {

    override fun observeMonthSchedule(): Flow<SnapshotState<MonthSchedule>> =
        snapshot.observe()

    override suspend fun refreshMonthSchedule(): RefreshResult =
        snapshot.refresh()
}
