package com.gabrielafonso.ipb.castelobranco.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gabrielafonso.ipb.castelobranco.data.local.entity.SundaySetEntity
import com.gabrielafonso.ipb.castelobranco.data.local.entity.SundaySongEntity
import kotlinx.coroutines.flow.Flow

data class SundaySetWithSongsRow(
    val date: String,
    val position: Int,
    val title: String,
    val artist: String,
    val tone: String
)

@Dao
interface SundaySongsDao {

    @Query(
        """
        SELECT s.date AS date, ss.position AS position, ss.title AS title, ss.artist AS artist, ss.tone AS tone
        FROM sunday_set s
        JOIN sunday_song ss ON ss.sundayDate = s.date
        ORDER BY s.date DESC, ss.position ASC
        """
    )
    fun observeAllRows(): Flow<List<SundaySetWithSongsRow>>

    @Query("DELETE FROM sunday_set")
    suspend fun clearAllSets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSets(items: List<SundaySetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSongs(items: List<SundaySongEntity>)

    @Transaction
    suspend fun replaceSnapshot(
        sets: List<SundaySetEntity>,
        songs: List<SundaySongEntity>
    ) {
        clearAllSets()
        upsertSets(sets)
        upsertSongs(songs)
    }
}
