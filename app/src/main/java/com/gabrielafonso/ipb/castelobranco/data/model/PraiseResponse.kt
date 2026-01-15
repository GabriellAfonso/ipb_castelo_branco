package com.gabrielafonso.ipb.castelobranco.data.model

data class PraiseResponse(
    val last_songs: List<LastSong>,
    val top_songs: List<TopSong>,
    val top_tones: List<TopTone>,
    val suggested_songs: Map<String, SuggestedSong> // Usamos Map porque as chaves são "1", "2", etc.
)

data class LastSong(
    val music: MusicDetails,
    val date: String,
    val tone: String,
    val position: Int
)

data class MusicDetails(
    val id: Int,
    val title: String,
    val artist: String
)

data class TopSong(
    val music__title: String,
    val play_count: Int
)

data class TopTone(
    val tone: String,
    val tone_count: Int
)

data class SuggestedSong(
    val music__id: Int,
    val music__title: String,
    val music__artist: String,
    val tone: String
)