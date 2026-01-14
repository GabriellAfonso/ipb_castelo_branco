package com.gabrielafonso.ipb.castelobranco.ui.components

import com.gabrielafonso.ipb.castelobranco.ui.tables.TableHeader
import com.gabrielafonso.ipb.castelobranco.ui.tables.TableRow
import com.gabrielafonso.ipb.castelobranco.ui.tables.TablesTabs

import androidx.compose.foundation.lazy.LazyListScope
import com.gabrielafonso.ipb.castelobranco.data.model.SongRow
import com.gabrielafonso.ipb.castelobranco.data.model.TableView
import com.gabrielafonso.ipb.castelobranco.ui.tables.DateHeader

fun LazyListScope.PraiseTables(
    currentView: TableView,
    onViewChange: (TableView) -> Unit,
    data: List<SongRow>
) {
    item {
        TablesTabs(
            selected = currentView,
            onSelect = onViewChange
        )
    }

    item {
        TableHeader(currentView)
    }

    if (currentView == TableView.LAST_SONGS) {
        val items = buildLastSongsItems(data)

        items.forEach { item ->
            when (item) {
                is LastSongItem.DateHeader -> {
                    item {
                        DateHeader(item.date)
                    }
                }

                is LastSongItem.Song -> {
                    item {
                        TableRow(
                            view = currentView,
                            row = item.row.copy(index = item.dayIndex
                            ))

                    }
                }
            }
        }
    } else {
        data.forEach { row ->
            item {
                TableRow(currentView, row)
            }
        }
    }
}


sealed class LastSongItem {

    data class DateHeader(
        val date: String
    ) : LastSongItem()

    data class Song(
        val row: SongRow,
        val dayIndex: Int
    ) : LastSongItem()
}

fun buildLastSongsItems(data: List<SongRow>): List<LastSongItem> =
    data
        .groupBy { it.date ?: "Sem data" }
        .flatMap { (date, songs) ->
            listOf(LastSongItem.DateHeader(date)) +
                    songs.mapIndexed { index, song ->
                        LastSongItem.Song(
                            row = song,
                            dayIndex = index + 1
                        )
                    }
        }