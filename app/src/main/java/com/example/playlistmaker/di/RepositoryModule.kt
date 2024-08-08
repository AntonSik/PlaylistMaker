package com.example.playlistmaker.di

import com.example.playlistmaker.data.repository.SearchTracksRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.SharingResourceRepositoryImpl
import com.example.playlistmaker.domain.repository.SearchTracksRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.SharingResourceRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchTracksRepository> {
        SearchTracksRepositoryImpl(
            context = androidContext(),
            networkClient = get(),
            localStorage = get()
        )
    }
    single<SharingResourceRepository> {
        SharingResourceRepositoryImpl(context = androidContext())
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefs = get())
    }

}