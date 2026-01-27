package com.gabrielafonso.ipb.castelobranco.data.repository

import android.util.Log
import com.gabrielafonso.ipb.castelobranco.data.api.BackendApi
import com.gabrielafonso.ipb.castelobranco.data.api.MonthScheduleDto
import com.gabrielafonso.ipb.castelobranco.data.local.JsonSnapshotStorage
import com.gabrielafonso.ipb.castelobranco.domain.model.MonthSchedule
import com.gabrielafonso.ipb.castelobranco.domain.model.ScheduleEntry
import com.gabrielafonso.ipb.castelobranco.domain.model.ScheduleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MonthScheduleRepositoryImpl(
    private val api: BackendApi,
    private val jsonStorage: JsonSnapshotStorage
) {
    companion object {
        private const val TAG = "MonthScheduleRepositoryImpl"
        private const val KEY_MONTH_SCHEDULE = "month_schedule_current"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
    }

    private val bump = MutableStateFlow(0)

    private fun mapToDomain(dto: MonthScheduleDto): MonthSchedule =
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeMonthSchedule(): Flow<MonthSchedule?> =
        bump.flatMapLatest {
            flow {
                emit(null)

                val cachedJson = runCatching { jsonStorage.loadOrNull(KEY_MONTH_SCHEDULE) }
                    .onFailure { Log.e(TAG, "Falha ao ler snapshot $KEY_MONTH_SCHEDULE", it) }
                    .getOrNull()

                if (!cachedJson.isNullOrBlank()) {
                    runCatching {
                        val cachedDto = json.decodeFromString(MonthScheduleDto.serializer(), cachedJson)
                        emit(mapToDomain(cachedDto))
                    }.onFailure { e ->
                        Log.e(TAG, "Falha ao parsear snapshot $KEY_MONTH_SCHEDULE", e)
                    }
                }

                runCatching {
                    val lastETag = runCatching { jsonStorage.loadETagOrNull(KEY_MONTH_SCHEDULE) }.getOrNull()
                    val response = api.getMonthSchedule(ifNoneMatch = lastETag)

                    when {
                        response.code() == 304 -> {
                            Log.d(TAG, "observeMonthSchedule: 304 Not Modified")
                        }
                        response.isSuccessful -> {
                            val body = response.body()
                            if (body != null) {
                                val raw = json.encodeToString(MonthScheduleDto.serializer(), body)
                                jsonStorage.save(KEY_MONTH_SCHEDULE, raw)

                                val newETag = response.headers()["ETag"]?.trim()
                                if (!newETag.isNullOrBlank()) {
                                    jsonStorage.saveETag(KEY_MONTH_SCHEDULE, newETag)
                                }

                                emit(mapToDomain(body))
                                Log.d(TAG, "observeMonthSchedule: atualizou snapshot (200)")
                            } else {
                                Log.w(TAG, "observeMonthSchedule: 200 sem body")
                            }
                        }
                        else -> {
                            Log.w(TAG, "observeMonthSchedule: HTTP ${response.code()}")
                        }
                    }
                }.onFailure { e ->
                    Log.e(TAG, "Falha na atualização da API (observeMonthSchedule)", e)
                }
            }.flowOn(Dispatchers.IO)
        }

    suspend fun refreshMonthSchedule(): Boolean {
        val result = try {
            val lastETag = runCatching { jsonStorage.loadETagOrNull(KEY_MONTH_SCHEDULE) }.getOrNull()
            val response = api.getMonthSchedule(ifNoneMatch = lastETag)

            when {
                response.code() == 304 -> {
                    Log.d(TAG, "refreshMonthSchedule: 304 Not Modified")
                    true
                }
                response.isSuccessful -> {
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "refreshMonthSchedule: 200 sem body")
                        false
                    } else {
                        val raw = json.encodeToString(MonthScheduleDto.serializer(), body)
                        jsonStorage.save(KEY_MONTH_SCHEDULE, raw)

                        val newETag = response.headers()["ETag"]?.trim()
                        if (!newETag.isNullOrBlank()) {
                            jsonStorage.saveETag(KEY_MONTH_SCHEDULE, newETag)
                        }

                        Log.d(TAG, "refreshMonthSchedule: salvou snapshot (200)")
                        true
                    }
                }
                else -> {
                    Log.w(TAG, "refreshMonthSchedule: HTTP ${response.code()}")
                    runCatching { !jsonStorage.loadOrNull(KEY_MONTH_SCHEDULE).isNullOrBlank() }.getOrDefault(false)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "refreshMonthSchedule: falhou rede, tentando ver snapshot", e)
            runCatching { !jsonStorage.loadOrNull(KEY_MONTH_SCHEDULE).isNullOrBlank() }.getOrDefault(false)
        }

        bump.update { it + 1 }
        return result
    }
}