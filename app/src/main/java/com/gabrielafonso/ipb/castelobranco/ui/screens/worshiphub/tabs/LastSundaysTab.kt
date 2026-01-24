package com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySet
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.components.Header

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import com.gabrielafonso.ipb.castelobranco.domain.model.SundaySetItem

@androidx.compose.runtime.Composable
fun LastSundaysTab(sundays: List<SundaySet>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Header(
            listOf(
                "#" to 0.9f,
                "Nome" to 4f,
                "Tom" to 1f,
                "Artista" to 2f
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(sundays) { sunday ->
                SundaySection(sunday = sunday)
            }
        }
    }
}


@Composable
fun SundaySection(sunday: SundaySet) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFd1e7dd))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sunday.date,
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }

        sunday.songs.forEachIndexed { index, song ->
            val isLast = index == sunday.songs.lastIndex
            SundaySongRow(
                song = song,
                modifier = Modifier.padding(bottom = if (isLast) 15.dp else 0.dp)
            )
        }
    }
}

@Composable
fun SundaySongRow(
    song: SundaySetItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(start = 10.dp)
    ) {
        Text(text = song.position.toString(), modifier = Modifier.weight(0.9f))
        Text(text = song.title, modifier = Modifier.weight(4.5f))
        Text(text = song.tone, modifier = Modifier.weight(1f))
        Text(
            text = song.artist,
            modifier = Modifier
                .weight(2f)
                .padding(end = 3.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
