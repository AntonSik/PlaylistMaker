package com.example.playlistmaker.ui.search.models

import com.example.playlistmaker.domain.models.Track

sealed interface SearchState{

    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ): SearchState

    data class Error(
        val errorMessage: String,
        val errorMessageExtra: String
    ):SearchState
    data class Empty(
        val message: String
    ):SearchState
}



