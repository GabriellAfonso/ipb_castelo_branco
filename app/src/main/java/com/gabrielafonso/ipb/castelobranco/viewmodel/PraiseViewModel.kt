package com.gabrielafonso.ipb.castelobranco.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabrielafonso.ipb.castelobranco.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import android.util.Log
import com.gabrielafonso.ipb.castelobranco.data.model.PraiseResponse
import com.gabrielafonso.ipb.castelobranco.data.model.SongRow
import com.gabrielafonso.ipb.castelobranco.data.model.TableView
import com.gabrielafonso.ipb.castelobranco.data.repository.PraiseRepository
import com.gabrielafonso.ipb.castelobranco.utils.DiskCache
import com.google.gson.Gson
class PraiseViewModel(private val repository: PraiseRepository, private val context: Context) : ViewModel() {
    private val _rows = MutableLiveData<List<SongRow>>()
    val rows: LiveData<List<SongRow>> = _rows
    private val gson = Gson()
    private val cacheFileName = "praise_response.json"

    fun fetchData(view: TableView) {
        viewModelScope.launch {
//            val shouldUpdate = !DiskCache.isCacheValidToday(context, cacheFileName)
            val shouldUpdate = !DiskCache.isCacheValidWithinMinutes(context, cacheFileName, 1)

            val cachedJson = DiskCache.load(context, cacheFileName)
            val cachedResponse = cachedJson?.let { gson.fromJson(it, PraiseResponse::class.java) }

            val rows = if (cachedResponse != null && !shouldUpdate) {
                // Cache do mesmo dia, usa direto
                mapToSongRows(cachedResponse, view)
            } else {
                // Busca da API e atualiza cache
                val response = repository.getPraiseData()
                response?.let {
                    DiskCache.save(context, cacheFileName, gson.toJson(it))
                    mapToSongRows(it, view)
                } ?: emptyList()
            }

            _rows.value = rows
        }
    }


    private fun mapToSongRows(response: PraiseResponse, view: TableView): List<SongRow> {
        return when (view) {
            TableView.LAST_SONGS -> response.last_songs.map { SongRow(it.position, it.music.title, it.tone, it.date, it.music.artist) }
            TableView.TOP_SONGS -> response.top_songs.mapIndexed { index, it -> SongRow(index + 1, it.music__title, count = it.play_count) }
            TableView.TOP_TONES -> response.top_tones.mapIndexed { index, it -> SongRow(index + 1, "", it.tone, count = it.tone_count) }
            TableView.SUGGESTED_SONGS -> response.suggested_songs.values.mapIndexed { index, it -> SongRow(index + 1, it.music__title, it.tone, artist = it.music__artist) }
        }
    }
}

