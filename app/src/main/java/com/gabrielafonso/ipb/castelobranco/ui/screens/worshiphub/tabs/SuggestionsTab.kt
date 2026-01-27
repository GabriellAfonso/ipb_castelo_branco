// app/src/main/java/com/gabrielafonso/ipb/castelobranco/ui/screens/worshiphub/tabs/SuggestionsTab.kt
package com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabrielafonso.ipb.castelobranco.domain.model.SuggestedSong
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.WorshipHubViewModel
import com.gabrielafonso.ipb.castelobranco.ui.screens.worshiphub.components.Header

@Composable
fun SuggestionsTab(
    suggestedSongs: List<SuggestedSong>,
    viewModel: WorshipHubViewModel
) {
    val isRefreshing by viewModel.isRefreshingSuggestedSongs.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            listOf(
                "#" to 0.9f,
                "Nome" to 4f,
                "Tom" to 1f,
                "Artista" to 2f
            )
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                itemsIndexed(suggestedSongs) { index, song ->
                    SuggestionsRow(
                        index = index,
                        song = song,
                        isRefreshing = isRefreshing
                    )
                }
            }

            if (isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // espaço restante da tela; botão centrado verticalmente aqui
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .weight(1f),

        ) {
            Button(
                onClick = { viewModel.refreshSuggestedSongs() },
                enabled = !isRefreshing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                if (!isRefreshing) {
                    Text(text = "Atualizar")
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun SuggestionsRow(
    index: Int,
    song: SuggestedSong,
    isRefreshing: Boolean
) {
    if (isRefreshing) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        )
        return
    }

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFd1e7dd))
            .padding(start = 10.dp)
    ) {
        Text(text = song.position.toString(), modifier = Modifier.weight(0.9f))
        Text(text = song.title, modifier = Modifier.weight(4f))
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
