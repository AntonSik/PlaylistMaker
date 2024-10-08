package com.example.playlistmaker.ui.media.models

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlaylistState {
    object Loading : PlaylistState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistState

    data class Empty(
        val message: String
    ) : PlaylistState
}