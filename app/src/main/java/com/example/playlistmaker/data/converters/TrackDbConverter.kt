package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.storage.db.entity.PlayListTrackEntity
import com.example.playlistmaker.data.storage.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {

    fun mapTrackToPlaylistTrackEntity(track: Track): PlayListTrackEntity{
        return PlayListTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName,
            track.releaseDate,
            track.country,
            track.previewUrl,

            )
    }

    fun mapTrackToEntity(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName,
            track.releaseDate,
            track.country,
            track.previewUrl,

            )
    }

    fun mapEntityToTrack(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName,
            track.releaseDate,
            track.country,
            track.previewUrl,

            )
    }

}