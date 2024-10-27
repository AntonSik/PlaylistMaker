package com.example.playlistmaker.presentation.media

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.FavoriteTracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.media.favorites.models.FavoriteState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val context: Context,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()
    fun observe(): LiveData<FavoriteState> = stateLiveData

    fun fillData() {
        render(FavoriteState.Loading)
        viewModelScope.launch {
            favoriteTracksInteractor
                .getAllFavoriteTracks()
                .collect { tracks ->
                    showData(tracks)
                }
        }
    }

    private fun showData(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            render(FavoriteState.Empty(context.getString(R.string.empty_favorite_tracks_placeholder_message)))
        } else {
            render(FavoriteState.Content(tracks))
        }
    }

    private fun render(state: FavoriteState) {
        stateLiveData.postValue(state)
    }
}