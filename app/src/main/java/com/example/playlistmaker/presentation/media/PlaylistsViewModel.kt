package com.example.playlistmaker.presentation.media

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.ui.media.models.PlaylistState
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val context: Context,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>()
    val stateLive: LiveData<PlaylistState> = stateLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistLive: LiveData<List<Playlist>> = playlistsLiveData

    fun fillData(){

        viewModelScope.launch {
            interactor.getAllPlaylists()
                .onStart { render(PlaylistState.Loading) }
                .collect{playlists->
                    showData(playlists)
                }
        }
    }


    private fun showData(playlists: List<Playlist>){
        if (playlists.isEmpty()){
            render(PlaylistState.Empty(context.getString(R.string.empty_playlists_placeholder_message)))
        }else{
            render(PlaylistState.Content(playlists))
        }
    }

    private fun render(state: PlaylistState){
        stateLiveData.postValue(state)
    }

}