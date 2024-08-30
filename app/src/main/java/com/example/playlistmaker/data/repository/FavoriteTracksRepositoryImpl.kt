package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.storage.db.TracksDatabase
import com.example.playlistmaker.data.storage.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val tracksDatabase: TracksDatabase,
    private val converter: TrackDbConverter
): FavoriteTracksRepository {
    override suspend fun addFavoriteTrack(track: Track) {
        val trackEntity = converter.mapTrackToEntity(track)
        tracksDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        val trackEntity = converter.mapTrackToEntity(track)
        tracksDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return tracksDatabase.trackDao().getTracks().map { convertFromTrackEntity(it) }

    }

    override suspend fun isFavoriteCheck(trackId: Int): Boolean {
        return tracksDatabase.trackDao().getFavoritesTracksId().contains(trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track>{
        return tracks.map { track -> converter.mapEntityToTrack(track) }
    }


}