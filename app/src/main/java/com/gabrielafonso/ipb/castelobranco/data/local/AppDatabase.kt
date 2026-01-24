package com.gabrielafonso.ipb.castelobranco.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gabrielafonso.ipb.castelobranco.data.local.dao.SundaySongsDao
import com.gabrielafonso.ipb.castelobranco.data.local.entity.SundaySetEntity
import com.gabrielafonso.ipb.castelobranco.data.local.entity.SundaySongEntity

@Database(
    entities = [
        SundaySetEntity::class,
        SundaySongEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sundaySongsDao(): SundaySongsDao
}
