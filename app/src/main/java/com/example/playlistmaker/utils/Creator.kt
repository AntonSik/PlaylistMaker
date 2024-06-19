package com.example.playlistmaker.utils

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchTracksRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.SharingResourceRepositoryImpl
import com.example.playlistmaker.data.storage.LocalStorage
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.SearchTrackInteracktor
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.SearchTracksRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.SharingResourceRepository
import com.example.playlistmaker.domain.usecase.PlayerInteractorImpl
import com.example.playlistmaker.domain.usecase.SearchTrackInteractorImpl
import com.example.playlistmaker.domain.usecase.SharingInteractorImpl
import com.example.playlistmaker.utils.App.Companion.SHARED_PREFERENCES


object Creator {
    private fun getSearchTracksRepository(context: Context): SearchTracksRepository {
        return SearchTracksRepositoryImpl(
            RetrofitNetworkClient(context),
            LocalStorage(context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)),
        )
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    private fun getSharingResourceRepository(context: Context): SharingResourceRepository {
        return SharingResourceRepositoryImpl(context)
    }

    fun provideSearchTracksInteractor(context: Context): SearchTrackInteracktor {
        return SearchTrackInteractorImpl(getSearchTracksRepository(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getSharingResourceRepository(context))
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

}