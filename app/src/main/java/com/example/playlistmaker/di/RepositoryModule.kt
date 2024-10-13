package com.example.playlistmaker.di

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.data.repository.SearchTracksRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.SharingResourceRepositoryImpl
import com.example.playlistmaker.data.storage.db.TracksDatabase
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.example.playlistmaker.domain.repository.SearchTracksRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.SharingResourceRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory { TrackDbConverter() }
    factory { PlaylistDbConverter() }

    single<SearchTracksRepository> {
        SearchTracksRepositoryImpl(
            context = androidContext(),
            networkClient = get(),
            localStorage = get(),
            tracksDatabase = get()
        )
    }
    single<SharingResourceRepository> {
        SharingResourceRepositoryImpl(context = androidContext())
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefs = get())
    }
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(trackDao = get<TracksDatabase>().trackDao(), converter = get())
    }
    single<PlaylistsRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get<TracksDatabase>().playlistDao(),
            playlistTrackDao = get<TracksDatabase>().playlistTrackDao(),
            playlistConverter = get(),
            trackConverter = get(),
            gson = get()
        )
    }

}