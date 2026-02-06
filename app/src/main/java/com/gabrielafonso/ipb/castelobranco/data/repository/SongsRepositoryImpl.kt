// app/src/main/java/com/gabrielafonso/ipb/castelobranco/data/repository/SongsRepositoryImpl.kt
package com.gabrielafonso.ipb.castelobranco.data.repository

import com.gabrielafonso.ipb.castelobranco.data.api.BackendApi
import com.gabrielafonso.ipb.castelobranco.data.api.SongsBySundayDto
import com.gabrielafonso.ipb.castelobranco.data.api.SuggestedSongDto
import com.gabrielafonso.ipb.castelobranco.data.api.TopSongDto
import com.gabrielafonso.ipb.castelobranco.data.api.TopToneDto
import com.gabrielafonso.ipb.castelobranco.data.local.JsonSnapshotStorage
import com.gabrielafonso.ipb.castelobranco.data.repository.base.BaseListSnapshotRepository
import com.gabrielafonso.ipb.castelobranco.domain.model.SuggestedSong
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySetItem
import com.gabrielafonso.ipb.castelobranco.domain.model.TopSong
import com.gabrielafonso.ipb.castelobranco.domain.model.TopTone
import com.gabrielafonso.ipb.castelobranco.domain.repository.SongsRepository
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val api: BackendApi,
    private val jsonStorage: JsonSnapshotStorage
) : SongsRepository {

    companion object {
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

    private val songsBySundayRepo = object : BaseListSnapshotRepository<SongsBySundayDto, SundaySet>(
        json = json,
        jsonStorage = jsonStorage,
        dtoListSerializer = ListSerializer(SongsBySundayDto.serializer()),
        key = KEY_SONGS_BY_SUNDAY,
        tag = "observeSongsBySunday",
        fetchNetwork = { ifNoneMatch -> api.getSongsBySunday(ifNoneMatch) }
    ) {
        override fun mapToDomain(dto: List<SongsBySundayDto>): List<SundaySet> =
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
    }

    private val topSongsRepo = object : BaseListSnapshotRepository<TopSongDto, TopSong>(
        json = json,
        jsonStorage = jsonStorage,
        dtoListSerializer = ListSerializer(TopSongDto.serializer()),
        key = KEY_TOP_SONGS,
        tag = "observeTopSongs",
        fetchNetwork = { ifNoneMatch -> api.getTopSongs(ifNoneMatch) }
    ) {
        override fun mapToDomain(dto: List<TopSongDto>): List<TopSong> =
            dto.map { TopSong(title = it.title, playCount = it.playCount) }
    }

    private val topTonesRepo = object : BaseListSnapshotRepository<TopToneDto, TopTone>(
        json = json,
        jsonStorage = jsonStorage,
        dtoListSerializer = ListSerializer(TopToneDto.serializer()),
        key = KEY_TOP_TONES,
        tag = "observeTopTones",
        fetchNetwork = { ifNoneMatch -> api.getTopTones(ifNoneMatch) }
    ) {
        override fun mapToDomain(dto: List<TopToneDto>): List<TopTone> =
            dto.map { TopTone(tone = it.tone, count = it.count) }
    }

    private val suggestedRepo = object : BaseListSnapshotRepository<SuggestedSongDto, SuggestedSong>(
        json = json,
        jsonStorage = jsonStorage,
        dtoListSerializer = ListSerializer(SuggestedSongDto.serializer()),
        key = KEY_SUGGESTED_SONGS,
        tag = "observeSuggestedSongs",
        // IMPORTANTE: impedir que o observe faça GET "solto" por trás
        fetchNetwork = { _ ->
            throw IllegalStateException(
                "Não buscar rede em observeSuggestedSongs(). Use refreshSuggestedSongs(fixedByPosition)."
            )
        }
    ) {
        override fun mapToDomain(dto: List<SuggestedSongDto>): List<SuggestedSong> =
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
    }

    override fun observeSongsBySunday() = songsBySundayRepo.observeSnapshotList()
    override suspend fun refreshSongsBySunday() = songsBySundayRepo.refreshSnapshotList()

    override fun observeTopSongs() = topSongsRepo.observeSnapshotList()
    override suspend fun refreshTopSongs() = topSongsRepo.refreshSnapshotList()

    override fun observeTopTones() = topTonesRepo.observeSnapshotList()
    override suspend fun refreshTopTones() = topTonesRepo.refreshSnapshotList()

    override fun observeSuggestedSongs() = suggestedRepo.observeSnapshotList()

    override suspend fun refreshSuggestedSongs(): Boolean {
        return refreshSuggestedSongs(emptyMap())
    }
    override suspend fun refreshSuggestedSongs(fixedByPosition: Map<Int, Int>): Boolean {
        val fixedParam = fixedByPosition
            .toList()
            .sortedBy { (pos, _) -> pos }
            .joinToString(separator = ",") { (pos, playedId) -> "$pos:$playedId" }

        val response = api.getSuggestedSongs(
            ifNoneMatch = null,
            fixed = fixedParam.ifBlank { null }
        )
        if (!response.isSuccessful) return false

        val body = response.body() ?: return false

        val rawDtoJson = json.encodeToString(
            ListSerializer(SuggestedSongDto.serializer()),
            body
        )
        jsonStorage.save(KEY_SUGGESTED_SONGS, rawDtoJson)

        val newETag = response.headers()["ETag"]?.trim()
        if (!newETag.isNullOrBlank()) {
            jsonStorage.saveETag(KEY_SUGGESTED_SONGS, newETag)
        }

        suggestedRepo.refreshSnapshotList()
        return true
    }
}
