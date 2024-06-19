package com.example.playlistmaker.ui.audioPlayer.models

sealed class PlayerScreenState {
    object Loading : PlayerScreenState()

    data class Content(
        val playerModel: PlayerModel
    ) : PlayerScreenState()
}