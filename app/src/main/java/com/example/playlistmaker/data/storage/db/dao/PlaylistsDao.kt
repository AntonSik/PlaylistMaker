package com.example.playlistmaker.data.storage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.storage.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_table WHERE playlistId =:playlistId")
    suspend fun deletePlaylist(playlistId: Int)

    @Update
    suspend fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity

    @Query("UPDATE playlist_table SET trackIds = :trackIds, trackCount = :trackCount WHERE playlistId = :playlistId")
    suspend fun updateTrackIds(playlistId: Int, trackIds: String, trackCount: Int)
}
