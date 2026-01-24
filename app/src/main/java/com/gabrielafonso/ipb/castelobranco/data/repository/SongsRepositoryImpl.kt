// app/src/main/java/com/gabrielafonso/ipb/castelobranco/data/repository/SongsRepositoryImpl.kt
package com.gabrielafonso.ipb.castelobranco.data.repository

import android.util.Log
import com.gabrielafonso.ipb.castelobranco.data.api.SongsApi
import com.gabrielafonso.ipb.castelobranco.data.api.SongsBySundayDto
import com.gabrielafonso.ipb.castelobranco.data.api.TopSongDto
import com.gabrielafonso.ipb.castelobranco.data.api.TopToneDto
import com.gabrielafonso.ipb.castelobranco.data.local.AppDatabase
import com.gabrielafonso.ipb.castelobranco.data.local.JsonSnapshotStorage
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySetItem
import com.gabrielafonso.ipb.castelobranco.domain.model.TopSong
import com.gabrielafonso.ipb.castelobranco.domain.model.TopTone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class SongsRepositoryImpl(
    private val api: SongsApi,
    private val db: AppDatabase,
    private val jsonStorage: JsonSnapshotStorage
) {
    companion object {
        private const val TAG = "SongsRepositoryImpl"
        private const val KEY_SONGS_BY_SUNDAY = "songs_by_sunday"
        private const val KEY_TOP_SONGS = "top_songs"
        private const val KEY_TOP_TONES = "top_tones"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
    }

    private fun mapToDomain(dto: List<SongsBySundayDto>): List<SundaySet> =
        dto.map { day ->
            SundaySet(
                date = day.date,
                songs = day.songs.map { s ->
                    SundaySetItem(
                        position = s.position,
                        title = s.title,
                        artist = s.artist,
                        tone = s.tone
                    )
                }
            )
        }

    private fun mapTopSongsToDomain(dto: List<TopSongDto>): List<TopSong> =
        dto.map { TopSong(title = it.title, playCount = it.playCount) }

    private fun mapTopTonesToDomain(dto: List<TopToneDto>): List<TopTone> =
        dto.map { TopTone(tone = it.tone, count = it.count) }

    private fun <Dto, Domain> observeSnapshotList(
        key: String,
        dtoListSerializer: KSerializer<List<Dto>>,
        tag: String,
        fetchNetwork: suspend () -> List<Dto>,
        mapToDomain: (List<Dto>) -> List<Domain>
    ): Flow<List<Domain>> =
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
                val freshDto = fetchNetwork()
                val raw = json.encodeToString(dtoListSerializer, freshDto)
                jsonStorage.save(key, raw)
                emit(mapToDomain(freshDto))
            }.onFailure { e ->
                Log.e(TAG, "Falha na atualização da API ($tag)", e)
            }
        }.flowOn(Dispatchers.IO)

    private suspend fun <Dto> refreshSnapshotList(
        key: String,
        dtoListSerializer: KSerializer<List<Dto>>,
        tag: String,
        fetchNetwork: suspend () -> List<Dto>
    ): Boolean {
        return try {
            val freshDto = fetchNetwork()
            val raw = json.encodeToString(dtoListSerializer, freshDto)
            jsonStorage.save(key, raw)
            Log.d(TAG, "$tag: salvou snapshot (rede)")
            true
        } catch (e: Exception) {
            Log.w(TAG, "$tag: falhou rede, tentando ver snapshot", e)
            val hasCache = runCatching { !jsonStorage.loadOrNull(key).isNullOrBlank() }
                .getOrDefault(false)

            if (hasCache) {
                Log.d(TAG, "$tag: sem rede, mas há snapshot")
                true
            } else {
                Log.w(TAG, "$tag: sem rede e sem snapshot")
                false
            }
        }
    }

    // ===== SongsBySunday =====

    fun observeSongsBySunday(): Flow<List<SundaySet>> =
        observeSnapshotList(
            key = KEY_SONGS_BY_SUNDAY,
            dtoListSerializer = ListSerializer(SongsBySundayDto.serializer()),
            tag = "observeSongsBySunday",
            fetchNetwork = { api.getSongsBySunday() },
            mapToDomain = { mapToDomain(it) }
        )

    suspend fun refreshSongsBySunday(): Boolean =
        refreshSnapshotList(
            key = KEY_SONGS_BY_SUNDAY,
            dtoListSerializer = ListSerializer(SongsBySundayDto.serializer()),
            tag = "refreshSongsBySunday",
            fetchNetwork = { api.getSongsBySunday() }
        )

    // ===== TopSongs =====

    fun observeTopSongs(): Flow<List<TopSong>> =
        observeSnapshotList(
            key = KEY_TOP_SONGS,
            dtoListSerializer = ListSerializer(TopSongDto.serializer()),
            tag = "observeTopSongs",
            fetchNetwork = { api.getTopSongs() },
            mapToDomain = { mapTopSongsToDomain(it) }
        )

    suspend fun refreshTopSongs(): Boolean =
        refreshSnapshotList(
            key = KEY_TOP_SONGS,
            dtoListSerializer = ListSerializer(TopSongDto.serializer()),
            tag = "refreshTopSongs",
            fetchNetwork = { api.getTopSongs() }
        )

    // ===== TopTones (igual ao TopSongs, sem get) =====

    fun observeTopTones(): Flow<List<TopTone>> =
        observeSnapshotList(
            key = KEY_TOP_TONES,
            dtoListSerializer = ListSerializer(TopToneDto.serializer()),
            tag = "observeTopTones",
            fetchNetwork = { api.getTopTones() },
            mapToDomain = { mapTopTonesToDomain(it) }
        )

    suspend fun refreshTopTones(): Boolean =
        refreshSnapshotList(
            key = KEY_TOP_TONES,
            dtoListSerializer = ListSerializer(TopToneDto.serializer()),
            tag = "refreshTopTones",
            fetchNetwork = { api.getTopTones() }
        )
}
