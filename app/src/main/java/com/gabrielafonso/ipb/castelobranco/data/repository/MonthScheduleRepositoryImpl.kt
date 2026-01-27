// app/src/main/java/com/gabrielafonso/ipb/castelobranco/data/repository/MonthScheduleRepositoryImpl.kt
package com.gabrielafonso.ipb.castelobranco.data.repository

import com.gabrielafonso.ipb.castelobranco.data.api.BackendApi
import com.gabrielafonso.ipb.castelobranco.data.api.MonthScheduleDto
import com.gabrielafonso.ipb.castelobranco.data.local.JsonSnapshotStorage
import com.gabrielafonso.ipb.castelobranco.data.repository.base.BaseSingleSnapshotRepository
import com.gabrielafonso.ipb.castelobranco.domain.model.MonthSchedule
import com.gabrielafonso.ipb.castelobranco.domain.model.ScheduleEntry
import com.gabrielafonso.ipb.castelobranco.domain.model.ScheduleItem
import com.gabrielafonso.ipb.castelobranco.domain.repository.MonthScheduleRepository
import kotlinx.serialization.json.Json

class MonthScheduleRepositoryImpl(
    private val api: BackendApi,
    private val jsonStorage: JsonSnapshotStorage
) : MonthScheduleRepository {

    companion object {
        private const val KEY_MONTH_SCHEDULE = "month_schedule_current"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
    }

    private val base = object : BaseSingleSnapshotRepository<MonthScheduleDto, MonthSchedule>(
        json = json,
        jsonStorage = jsonStorage,
        dtoSerializer = MonthScheduleDto.serializer(),
        key = KEY_MONTH_SCHEDULE,
        tag = "observeMonthSchedule",
        fetchNetwork = { ifNoneMatch -> api.getMonthSchedule(ifNoneMatch) }
    ) {
        override fun mapToDomain(dto: MonthScheduleDto): MonthSchedule =
            MonthSchedule(
                year = dto.year,
                month = dto.month,
                schedule = dto.schedule.mapValues { (_, entryDto) ->
                    ScheduleEntry(
                        time = entryDto.time.orEmpty(),
                        items = entryDto.items.map { i ->
                            ScheduleItem(
                                day = i.day,
                                member = i.member
                            )
                        }
                    )
                }
            )
    }

    override fun observeMonthSchedule() = base.observeSnapshot()
    override suspend fun refreshMonthSchedule() = base.refreshSnapshot()
}
