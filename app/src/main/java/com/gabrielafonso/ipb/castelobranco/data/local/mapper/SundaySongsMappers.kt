package com.gabrielafonso.ipb.castelobranco.data.local.mapper

import com.gabrielafonso.ipb.castelobranco.data.api.SongsBySundayDto
import com.gabrielafonso.ipb.castelobranco.data.local.dao.SundaySetWithSongsRow
import com.gabrielafonso.ipb.castelobranco.data.local.entity.SundaySetEntity
import com.gabrielafonso.ipb.castelobranco.data.local.entity.SundaySongEntity
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySetItem

data class SundaySnapshotEntities(
    val sets: List<SundaySetEntity>,
    val songs: List<SundaySongEntity>
)

fun List<SongsBySundayDto>.toSnapshotEntities(nowEpochMs: Long): SundaySnapshotEntities {
    val sets = this.map { day ->
        SundaySetEntity(
            date = day.date,
            cachedAtEpochMs = nowEpochMs
        )
    }

    val songs = this.flatMap { day ->
        day.songs.map { s ->
            SundaySongEntity(
                sundayDate = day.date,
                position = s.position,
                title = s.title,
                artist = s.artist,
                tone = s.tone
            )
        }
    }

    return SundaySnapshotEntities(sets = sets, songs = songs)
}

fun List<SundaySetWithSongsRow>.toDomain(): List<SundaySet> {
    return this
        .groupBy { it.date }
        .map { (date, rows) ->
            SundaySet(
                date = date,
                songs = rows
                    .sortedBy { it.position }
                    .map {
                        SundaySetItem(
                            position = it.position,
                            title = it.title,
                            artist = it.artist,
                            tone = it.tone
                        )
                    }
            )
        }
        .sortedByDescending { it.date }
}
