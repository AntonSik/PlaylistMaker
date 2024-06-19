package com.example.playlistmaker.ui.audioPlayer.models

import com.example.playlistmaker.domain.models.PlayerModel

sealed class PlayerScreenState {
    object Loading : PlayerScreenState()

    data class Content(
        val playerModel: PlayerModel
    ) : PlayerScreenState()
}