package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

   suspend fun addFavoriteTrack(track: Track)
   suspend fun deleteFavoriteTrack(track: Track)
    fun getAllFavoriteTracks(): Flow<List<Track>>
    suspend fun isFavoriteCheck(trackId: Int): Boolean
}