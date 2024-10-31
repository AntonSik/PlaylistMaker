package com.example.playlistmaker.ui.media.openedPlaylist.models

import com.example.playlistmaker.domain.models.PlaylistModel

sealed interface PlaylistScreenState {

    object Loading : PlaylistScreenState

    data class Content(
        val playlistModel: PlaylistModel
    ) : PlaylistScreenState
}