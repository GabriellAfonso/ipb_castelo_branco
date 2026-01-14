package com.gabrielafonso.ipb.castelobranco.data.model

data class SongRow(
    val index: Int,
    val name: String,
    val tone: String? = null,
    val date: String? = null,
    val artist: String? = null,
    val count: Int? = null
)
