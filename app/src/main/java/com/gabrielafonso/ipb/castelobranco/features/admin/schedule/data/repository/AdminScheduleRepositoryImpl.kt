package com.gabrielafonso.ipb.castelobranco.features.admin.schedule.data.repository

import com.gabrielafonso.ipb.castelobranco.features.admin.schedule.data.api.AdminScheduleApi
import com.gabrielafonso.ipb.castelobranco.features.admin.schedule.data.mapper.toDomain
import com.gabrielafonso.ipb.castelobranco.features.admin.schedule.domain.model.Member
import com.gabrielafonso.ipb.castelobranco.features.admin.schedule.domain.repository.AdminScheduleRepository
import com.gabrielafonso.ipb.castelobranco.features.schedule.data.dto.MonthScheduleDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminScheduleRepositoryImpl @Inject constructor(
    private val api: AdminScheduleApi
) : AdminScheduleRepository {

    override suspend fun getMembers(): Result<List<Member>> = runCatching {
        api.getMembers().members.map { it.toDomain() }
    }

    override suspend fun generateSchedule(year: Int, month: Int): Result<MonthScheduleDto> = runCatching {
        api.generateSchedule(year = year, month = month)
    }

    override suspend fun saveSchedule(year: Int, month: Int, schedule: MonthScheduleDto): Result<Unit> = runCatching {
        api.saveSchedule(schedule)
    }
}
