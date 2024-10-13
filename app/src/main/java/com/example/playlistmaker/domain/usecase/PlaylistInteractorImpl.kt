package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistInteractor {
    override suspend fun addNewPlaylist(playlist: Playlist) {
        repository.addNewPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun update(playlist: Playlist) {
        repository.update(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun checkTrackInPlaylist(trackId: Int, playlist: Playlist): Boolean {
        return repository.checkTrackInPlaylist(trackId, playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }
}