// app/src/main/java/com/gabrielafonso/ipb/castelobranco/data/api/AllSongsDto.kt
package com.gabrielafonso.ipb.castelobranco.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllSongDto(
    val id: Int,
    val title: String,
    val artist: String,
    @SerialName("category") val categoryName: String = ""
)
