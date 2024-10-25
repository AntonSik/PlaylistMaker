package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.storage.db.entity.PlayListTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlayListTrackEntity)

    @Query("DELETE FROM playlist_tracks_table Where trackId = :trackId")
    suspend fun deleteTrackFromTable(trackId: Int)

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Int>): List<PlayListTrackEntity>
}