package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.models.PlayerModel

class PlayerInteractorImpl(val repository: PlayerRepository) : PlayerInteractor {
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
            )
            onComplete(playerModel)
        }
    }


    override fun preparePlayer(recordsUrl: String?) {
        repository.preparePlayer(recordsUrl)
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun getDefault() {
        repository.getDefault()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun setOnCompletionCallback(callback: () -> Unit) {
        repository.setOnCompletionCallback(callback)
    }

}