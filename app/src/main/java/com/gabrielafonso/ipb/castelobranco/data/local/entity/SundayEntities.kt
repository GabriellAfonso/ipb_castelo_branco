package com.gabrielafonso.ipb.castelobranco.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "sunday_set")
data class SundaySetEntity(
    @PrimaryKey
    val date: String, // "dd/MM/yyyy" (igual ao backend)
    val cachedAtEpochMs: Long
)

@Entity(
    tableName = "sunday_song",
    primaryKeys = ["sundayDate", "position"],
    foreignKeys = [
        ForeignKey(
            entity = SundaySetEntity::class,
            parentColumns = ["date"],
            childColumns = ["sundayDate"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sundayDate"])
    ]
)
data class SundaySongEntity(
    val sundayDate: String, // FK -> SundaySetEntity.date
    val position: Int,
    val title: String,
    val artist: String,
    val tone: String
)
