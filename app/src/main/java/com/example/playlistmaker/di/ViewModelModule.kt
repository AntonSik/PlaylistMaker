package com.example.playlistmaker.di

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.audioPlayer.AudioPlayerViewModel
import com.example.playlistmaker.presentation.search.SearchTracksViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{

    viewModel{
        SettingsViewModel(application = androidApplication(), sharingInteractor =  get(), settingsRepository = get())
    }

    viewModel { (track : Track?)->
        AudioPlayerViewModel(application = androidApplication(), track = track, playerInteractor = get())
    }
    viewModel {
        SearchTracksViewModel(application = androidApplication(), searchInteractor =  get())
    }
}