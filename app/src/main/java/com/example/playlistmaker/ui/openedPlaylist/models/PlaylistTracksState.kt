package com.example.playlistmaker.ui.openedPlaylist.models

import com.example.playlistmaker.domain.models.Track


sealed interface PlaylistTracksState {
    object Loading : PlaylistTracksState

    data class Content(
        val trackList: List<Track>
    ) : PlaylistTracksState

    data class Empty(
        val message: String
    ) : PlaylistTracksState
}