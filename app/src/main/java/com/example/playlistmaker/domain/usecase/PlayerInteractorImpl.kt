package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.PlayerModel
import com.example.playlistmaker.domain.models.Track

class PlayerInteractorImpl : PlayerInteractor {
    override fun loadTrackData(track: Track?, onComplete: (PlayerModel) -> Unit) {
        track?.let {
            val playerModel = PlayerModel(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                primaryGenreName = track.primaryGenreName,
                releaseDate = track.releaseDate,
                country = track.country,
                previewUrl = track.previewUrl,
                isFavorite = track.isFavorite
            )
            onComplete(playerModel)
        }
    }

}