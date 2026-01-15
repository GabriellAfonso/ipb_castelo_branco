package com.gabrielafonso.ipb.castelobranco.data.model

fun SongRow.stableKey(): String =
    listOfNotNull(
        name,
        artist,
        tone,
        date
    ).joinToString("|")
