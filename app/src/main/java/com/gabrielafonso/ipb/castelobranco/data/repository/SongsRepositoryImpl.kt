package com.gabrielafonso.ipb.castelobranco.data.repository

import com.gabrielafonso.ipb.castelobranco.data.api.SongsApi
import com.gabrielafonso.ipb.castelobranco.domain.model.SuggestedSong
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySetItem
import com.gabrielafonso.ipb.castelobranco.domain.model.TopSong
import com.gabrielafonso.ipb.castelobranco.domain.model.TopTone

class SongsRepositoryImpl(
    private val api: SongsApi
) {

    suspend fun getSongsBySunday(): List<SundaySet>? {
        return try {
            val dto = api.getSongsBySunday()
            dto.map { day ->
                SundaySet(
                    date = day.date,
                    songs = day.songs.map {
                        SundaySetItem(
                            position = it.position,
                            title = it.title,
                            artist = it.artist,
                            tone = it.tone
                        )
                    }
                )
            }
        } catch (e: Exception) {
            // você pode logar aqui (Timber) ou propagar dependendo do estilo preferido
            null
        }
    }

    suspend fun getTopSongs(): List<TopSong>? {
        return try {
            val dto = api.getTopSongs()
            dto.map { TopSong(title = it.title, playCount = it.playCount) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getTopTones(): List<TopTone>? {
        return try {
            val dto = api.getTopTones()
            dto.map { TopTone(tone = it.tone, count = it.count) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSuggestedSongs(): List<SuggestedSong>? {
        return try {
            val dtoMap = api.getSuggestedSongs()
            dtoMap.values
                .sortedBy { it.position }
                .map {
                    SuggestedSong(
                        id = it.id,
                        songId = it.song.id,
                        title = it.song.title,
                        artist = it.song.artist,
                        date = it.date,
                        tone = it.tone,
                        position = it.position
                    )
                }
        } catch (e: Exception) {
            null
        }
    }
}