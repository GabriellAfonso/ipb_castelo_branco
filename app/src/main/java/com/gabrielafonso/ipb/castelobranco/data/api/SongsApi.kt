package com.gabrielafonso.ipb.castelobranco.data.api

import com.gabrielafonso.ipb.castelobranco.core.network.Endpoints
import retrofit2.http.GET

interface SongsApi {
    @GET(Endpoints.SONGS_BY_SUNDAY_PATH)
    suspend fun getSongsBySunday(): List<SongsBySundayDto>

    @GET(Endpoints.TOP_SONGS_PATH)
    suspend fun getTopSongs(): List<TopSongDto>

    @GET(Endpoints.TOP_TONES_PATH)
    suspend fun getTopTones(): List<TopToneDto>

    @GET(Endpoints.SUGGESTED_SONGS_PATH)
    suspend fun getSuggestedSongs(): Map<String, SuggestedSongDto>
}