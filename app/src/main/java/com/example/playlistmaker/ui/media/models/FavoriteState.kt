package com.example.playlistmaker.ui.media.models

import com.example.playlistmaker.domain.models.Track

sealed interface FavoriteState {

    object Loading: FavoriteState

    data class Content(
        val favoriteList: List<Track>
    ): FavoriteState

    data class Empty(
        val message: String
    ): FavoriteState
}