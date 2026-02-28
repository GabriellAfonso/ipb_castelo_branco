package com.gabrielafonso.ipb.castelobranco.features.admin.schedule.domain.repository

import com.gabrielafonso.ipb.castelobranco.features.admin.schedule.domain.model.Member
import com.gabrielafonso.ipb.castelobranco.features.schedule.data.dto.MonthScheduleDto

interface AdminScheduleRepository {
    suspend fun getMembers(): Result<List<Member>>
    suspend fun generateSchedule(year: Int, month: Int): Result<MonthScheduleDto>
    suspend fun saveSchedule(year: Int, month: Int, schedule: MonthScheduleDto): Result<Unit>
}
