package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

   suspend fun addFavoriteTrack(track: Track)
   suspend fun deleteFavoriteTrack(track: Track)
   suspend fun isFavoriteCheck(trackId: Int):Boolean
   fun getAllFavoriteTracks(): Flow<List<Track>>

}