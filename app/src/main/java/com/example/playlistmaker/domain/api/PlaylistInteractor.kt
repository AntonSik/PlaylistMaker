package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addNewPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun update(playlist: Playlist)
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun checkTrackInPlaylist(trackId: Int, playlist: Playlist): Boolean
    fun getAllPlaylists(): Flow<List<Playlist>>
}