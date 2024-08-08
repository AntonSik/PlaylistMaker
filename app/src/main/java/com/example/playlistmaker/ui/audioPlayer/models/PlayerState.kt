package com.example.playlistmaker.ui.audioPlayer.models

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val buttonType: Boolean,
    val progress: String
) {

    class Default : PlayerState(false, false, "00:00")

    class Prepared : PlayerState(true, false, "00:00")

    class Playing(progress: String) : PlayerState(true, true, progress)

    class Paused(progress: String) : PlayerState(true, false, progress)
}