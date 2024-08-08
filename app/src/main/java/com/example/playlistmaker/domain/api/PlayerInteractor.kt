package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.PlayerModel

interface PlayerInteractor {
    fun loadTrackData(track: Track?, onComplete: (PlayerModel) -> Unit)

}