package com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.tabs
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gabrielafonso.ipb.castelobranco.domain.model.TopSong
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.components.Header

@Composable
fun TopSongsTab(topSongs: List<TopSong>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Header(
            listOf(
                "#" to 0.8f,
                "Nome" to 3f,
                "Vezes" to 1f,
            )
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            itemsIndexed(topSongs) { index, song ->
                TopSongsRow(
                    index = index,
                    song = song
                )

            }
        }
    }
}


@Composable
fun TopSongsRow(
    index: Int,
    song: TopSong
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFd1e7dd))
        .padding(start = 10.dp)
    ) {
        Text(
            text = "${index + 1}",
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = song.title,
            modifier = Modifier.weight(3f)
        )
        Text(
            text = song.playCount.toString(),
            modifier = Modifier.weight(1f)
        )

    }
}