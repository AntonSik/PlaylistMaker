package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.storage.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks_table ORDER BY timeStamp DESC")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM tracks_table")
    suspend fun getFavoritesTracksId(): List<Int>
}