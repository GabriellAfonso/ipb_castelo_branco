package com.gabrielafonso.ipb.castelobranco.data.repository

import android.util.Log
import com.gabrielafonso.ipb.castelobranco.data.api.BackendApi
import com.gabrielafonso.ipb.castelobranco.data.api.SongsBySundayDto
import com.gabrielafonso.ipb.castelobranco.data.api.SuggestedSongDto
import com.gabrielafonso.ipb.castelobranco.data.api.TopSongDto
import com.gabrielafonso.ipb.castelobranco.data.api.TopToneDto
import com.gabrielafonso.ipb.castelobranco.data.local.JsonSnapshotStorage
import com.gabrielafonso.ipb.castelobranco.domain.model.SuggestedSong
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySetItem
import com.gabrielafonso.ipb.castelobranco.domain.model.TopSong
import com.gabrielafonso.ipb.castelobranco.domain.model.TopTone
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

class SongsRepositoryImpl(
    private val api: BackendApi,
    private val jsonStorage: JsonSnapshotStorage
) {
    companion object {
        private const val TAG = "SongsRepositoryImpl"
        private const val KEY_SONGS_BY_SUNDAY = "songs_by_sunday"
        private const val KEY_TOP_SONGS = "top_songs"
        private const val KEY_TOP_TONES = "top_tones"
        private const val KEY_SUGGESTED_SONGS = "suggested_songs"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
    }

    // bump único: qualquer refresh força re-emissão nos observers
    private val bump = MutableStateFlow(0)

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

    private fun mapSuggestedToDomain(dto: List<SuggestedSongDto>): List<SuggestedSong> =
        dto.map { s ->
            SuggestedSong(
                id = s.id,
                songId = s.song.id,
                title = s.song.title,
                artist = s.song.artist,
                date = s.date,
                tone = s.tone,
                position = s.position
            )
        }.sortedBy { it.position }

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
                        response.code() == 304 -> {
                            Log.d(TAG, "$tag: 304 Not Modified")
                        }
                        response.isSuccessful -> {
                            val body = response.body()
                            if (body != null) {
                                val raw = json.encodeToString(dtoListSerializer, body)
                                jsonStorage.save(key, raw)

                                val newETag = response.headers()["ETag"]?.trim()
                                if (!newETag.isNullOrBlank()) {
                                    jsonStorage.saveETag(key, newETag)
                                }

                                emit(mapToDomain(body))
                                Log.d(TAG, "$tag: atualizou snapshot (200)")
                            } else {
                                Log.w(TAG, "$tag: 200 sem body")
                            }
                        }
                        else -> {
                            Log.w(TAG, "$tag: HTTP ${response.code()}")
                        }
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
                        if (!newETag.isNullOrBlank()) {
                            jsonStorage.saveETag(key, newETag)
                        }

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

        // mantém lógica do refresh; só força re-emissão
        bump.update { it + 1 }
        return result
    }

    fun observeSongsBySunday(): Flow<List<SundaySet>> =
        observeSnapshotListWithETag(
            key = KEY_SONGS_BY_SUNDAY,
            dtoListSerializer = ListSerializer(SongsBySundayDto.serializer()),
            tag = "observeSongsBySunday",
            fetchNetwork = { ifNoneMatch -> api.getSongsBySunday(ifNoneMatch) },
            mapToDomain = { mapToDomain(it) }
        )

    suspend fun refreshSongsBySunday(): Boolean =
        refreshSnapshotListWithETag(
            key = KEY_SONGS_BY_SUNDAY,
            dtoListSerializer = ListSerializer(SongsBySundayDto.serializer()),
            tag = "refreshSongsBySunday",
            fetchNetwork = { ifNoneMatch -> api.getSongsBySunday(ifNoneMatch) }
        )

    fun observeTopSongs(): Flow<List<TopSong>> =
        observeSnapshotListWithETag(
            key = KEY_TOP_SONGS,
            dtoListSerializer = ListSerializer(TopSongDto.serializer()),
            tag = "observeTopSongs",
            fetchNetwork = { ifNoneMatch -> api.getTopSongs(ifNoneMatch) },
            mapToDomain = { mapTopSongsToDomain(it) }
        )

    suspend fun refreshTopSongs(): Boolean =
        refreshSnapshotListWithETag(
            key = KEY_TOP_SONGS,
            dtoListSerializer = ListSerializer(TopSongDto.serializer()),
            tag = "refreshTopSongs",
            fetchNetwork = { ifNoneMatch -> api.getTopSongs(ifNoneMatch) }
        )

    fun observeTopTones(): Flow<List<TopTone>> =
        observeSnapshotListWithETag(
            key = KEY_TOP_TONES,
            dtoListSerializer = ListSerializer(TopToneDto.serializer()),
            tag = "observeTopTones",
            fetchNetwork = { ifNoneMatch -> api.getTopTones(ifNoneMatch) },
            mapToDomain = { mapTopTonesToDomain(it) }
        )

    suspend fun refreshTopTones(): Boolean =
        refreshSnapshotListWithETag(
            key = KEY_TOP_TONES,
            dtoListSerializer = ListSerializer(TopToneDto.serializer()),
            tag = "refreshTopTones",
            fetchNetwork = { ifNoneMatch -> api.getTopTones(ifNoneMatch) }
        )

    fun observeSuggestedSongs(): Flow<List<SuggestedSong>> =
        observeSnapshotListWithETag(
            key = KEY_SUGGESTED_SONGS,
            dtoListSerializer = ListSerializer(SuggestedSongDto.serializer()),
            tag = "observeSuggestedSongs",
            fetchNetwork = { ifNoneMatch -> api.getSuggestedSongs(ifNoneMatch) },
            mapToDomain = { mapSuggestedToDomain(it) }
        )

    suspend fun refreshSuggestedSongs(): Boolean =
        refreshSnapshotListWithETag(
            key = KEY_SUGGESTED_SONGS,
            dtoListSerializer = ListSerializer(SuggestedSongDto.serializer()),
            tag = "refreshSuggestedSongs",
            fetchNetwork = { ifNoneMatch -> api.getSuggestedSongs(ifNoneMatch) }
        )
}
