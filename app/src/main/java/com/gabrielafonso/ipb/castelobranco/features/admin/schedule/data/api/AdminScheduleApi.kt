package com.gabrielafonso.ipb.castelobranco.features.admin.schedule.data.api

import com.gabrielafonso.ipb.castelobranco.features.admin.schedule.data.dto.MemberListDto
import com.gabrielafonso.ipb.castelobranco.features.schedule.data.dto.MonthScheduleDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AdminScheduleApi {

    @GET(AdminScheduleEndpoints.MEMBERS)
    suspend fun getMembers(): MemberListDto

    @GET(AdminScheduleEndpoints.GENERATE)
    suspend fun generateSchedule(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): MonthScheduleDto

    @POST(AdminScheduleEndpoints.SAVE)
    suspend fun saveSchedule(@Body body: MonthScheduleDto): Unit
}
