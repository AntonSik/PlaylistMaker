package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistModel
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addNewPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun checkTrackInPlaylist(trackId: Int, playlist: Playlist): Boolean

    suspend fun getTracksOfPlaylist(playlistId: Int): Flow<List<Track>>

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun loadPlaylistDataById(playlistId: Int, onComplete: (PlaylistModel) -> Unit)
}