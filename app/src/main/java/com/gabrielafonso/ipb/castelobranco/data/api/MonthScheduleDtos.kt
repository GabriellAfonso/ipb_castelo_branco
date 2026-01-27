package com.gabrielafonso.ipb.castelobranco.data.api

import kotlinx.serialization.Serializable

@Serializable
data class MonthScheduleDto(
    val year: Int,
    val month: Int,
    val schedule: Map<String, ScheduleEntryDto>
)

@Serializable
data class ScheduleEntryDto(
    val time: String,
    val items: List<ScheduleItemDto>
)

@Serializable
data class ScheduleItemDto(
    val day: Int,
    val member: String
)
