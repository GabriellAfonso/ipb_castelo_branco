// app/src/main/java/com/gabrielafonso/ipb/castelobranco/data/repository/HymnalRepositoryImpl.kt
package com.gabrielafonso.ipb.castelobranco.data.repository

import android.util.Log
import com.gabrielafonso.ipb.castelobranco.data.api.HymnDto
import com.gabrielafonso.ipb.castelobranco.data.api.BackendApi
import com.gabrielafonso.ipb.castelobranco.data.local.JsonSnapshotStorage
import com.gabrielafonso.ipb.castelobranco.domain.model.Hymn
import com.gabrielafonso.ipb.castelobranco.domain.model.HymnLyric
import com.gabrielafonso.ipb.castelobranco.domain.model.HymnLyricType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import retrofit2.Response

class HymnalRepositoryImpl(
    private val api: BackendApi,
    private val jsonStorage: JsonSnapshotStorage
) {
    companion object {
        private const val TAG = "HymnalRepositoryImpl"
        private const val KEY_HYMNAL = "hymnal"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
    }

    private val bump = MutableStateFlow(0)

    private fun lyricTypeOf(raw: String): HymnLyricType =
        when (raw.trim().lowercase()) {
            "verse" -> HymnLyricType.VERSE
            "chorus" -> HymnLyricType.CHORUS
            else -> HymnLyricType.OTHER
        }

    private fun mapToDomain(dto: List<HymnDto>): List<Hymn> =
        dto.map { h ->
            Hymn(
                number = h.number,
                title = h.title,
                lyrics = h.lyrics.map { l ->
                    HymnLyric(
                        type = lyricTypeOf(l.type),
                        text = l.text
                    )
                }
            )
        }.sortedWith(compareBy({ it.number.toIntOrNull() ?: Int.MAX_VALUE }, { it.number }))

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <Dto, Domain> observeSnapshotListWithETag(
        key: String,
        dtoListSerializer: KSerializer<List<Dto>>,
        tag: String,
        fetchNetwork: suspend (ifNoneMatch: String?) -> Response<List<Dto>>,
        mapToDomain: (List<Dto>) -> List<Domain>
    ): Flow<List<Domain>> =
        bump.flatMapLatest {
            flow {
                emit(emptyList())

                val cachedJson = runCatching { jsonStorage.loadOrNull(key) }
                    .onFailure { Log.e(TAG, "Falha ao ler snapshot $key", it) }
                    .getOrNull()

                if (!cachedJson.isNullOrBlank()) {
                    runCatching {
                        val cachedDto = json.decodeFromString(dtoListSerializer, cachedJson)
                        emit(mapToDomain(cachedDto))
                    }.onFailure { e ->
                        Log.e(TAG, "Falha ao parsear snapshot $key", e)
                    }
                }

                runCatching {
                    val lastETag = runCatching { jsonStorage.loadETagOrNull(key) }.getOrNull()
                    val response = fetchNetwork(lastETag)

                    when {
                        response.code() == 304 -> Log.d(TAG, "$tag: 304 Not Modified")
                        response.isSuccessful -> {
                            val body = response.body()
                            if (body != null) {
                                val raw = json.encodeToString(dtoListSerializer, body)
                                jsonStorage.save(key, raw)

                                val newETag = response.headers()["ETag"]?.trim()
                                if (!newETag.isNullOrBlank()) jsonStorage.saveETag(key, newETag)

                                emit(mapToDomain(body))
                                Log.d(TAG, "$tag: atualizou snapshot (200)")
                            } else {
                                Log.w(TAG, "$tag: 200 sem body")
                            }
                        }
                        else -> Log.w(TAG, "$tag: HTTP ${response.code()}")
                    }
                }.onFailure { e ->
                    Log.e(TAG, "Falha na atualização da API ($tag)", e)
                }
            }.flowOn(Dispatchers.IO)
        }

    private suspend fun <Dto> refreshSnapshotListWithETag(
        key: String,
        dtoListSerializer: KSerializer<List<Dto>>,
        tag: String,
        fetchNetwork: suspend (ifNoneMatch: String?) -> Response<List<Dto>>
    ): Boolean {
        val result = try {
            val lastETag = runCatching { jsonStorage.loadETagOrNull(key) }.getOrNull()
            val response = fetchNetwork(lastETag)

            when {
                response.code() == 304 -> {
                    Log.d(TAG, "$tag: 304 Not Modified")
                    true
                }
                response.isSuccessful -> {
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "$tag: 200 sem body")
                        false
                    } else {
                        val raw = json.encodeToString(dtoListSerializer, body)
                        jsonStorage.save(key, raw)

                        val newETag = response.headers()["ETag"]?.trim()
                        if (!newETag.isNullOrBlank()) jsonStorage.saveETag(key, newETag)

                        Log.d(TAG, "$tag: salvou snapshot (200)")
                        true
                    }
                }
                else -> {
                    Log.w(TAG, "$tag: HTTP ${response.code()}")
                    runCatching { !jsonStorage.loadOrNull(key).isNullOrBlank() }.getOrDefault(false)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "$tag: falhou rede, tentando ver snapshot", e)
            runCatching { !jsonStorage.loadOrNull(key).isNullOrBlank() }.getOrDefault(false)
        }

        bump.update { it + 1 }
        return result
    }

    fun observeHymnal(): Flow<List<Hymn>> =
        observeSnapshotListWithETag(
            key = KEY_HYMNAL,
            dtoListSerializer = ListSerializer(HymnDto.serializer()),
            tag = "observeHymnal",
            fetchNetwork = { ifNoneMatch -> api.getHymnal(ifNoneMatch) },
            mapToDomain = { mapToDomain(it) }
        )

    suspend fun refreshHymnal(): Boolean =
        refreshSnapshotListWithETag(
            key = KEY_HYMNAL,
            dtoListSerializer = ListSerializer(HymnDto.serializer()),
            tag = "refreshHymnal",
            fetchNetwork = { ifNoneMatch -> api.getHymnal(ifNoneMatch) }
        )
}
