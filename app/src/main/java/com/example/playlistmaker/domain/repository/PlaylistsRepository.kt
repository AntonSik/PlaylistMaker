package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.storage.db.entity.PlayListTrackEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun addNewPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int)
    suspend fun update(playlist: Playlist)
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun checkTrackInPlaylist(trackId: Int, playlist: Playlist): Boolean
    suspend fun checkRelationsAndDeleteTrackFromTable(trackId: Int)
    suspend fun getPlaylistById(playlistId: Int): Playlist

    suspend fun getTracksByIds(playlistId: Int): List<PlayListTrackEntity>
    fun getAllPlaylists(): Flow<List<Playlist>>
}