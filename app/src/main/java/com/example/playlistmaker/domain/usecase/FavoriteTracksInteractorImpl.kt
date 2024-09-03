package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
): FavoriteTracksInteractor {
    override suspend fun addFavoriteTrack(track: Track) {
        repository.addFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        repository.deleteFavoriteTrack(track)
    }

    override suspend fun isFavoriteCheck(trackId: Int): Boolean {
      return repository.isFavoriteCheck(trackId)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return repository.getAllFavoriteTracks()
    }
}