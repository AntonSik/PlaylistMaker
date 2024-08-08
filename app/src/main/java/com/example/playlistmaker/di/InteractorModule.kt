package com.example.playlistmaker.di

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.SearchTrackInteracktor
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.usecase.PlayerInteractorImpl
import com.example.playlistmaker.domain.usecase.SearchTrackInteractorImpl
import com.example.playlistmaker.domain.usecase.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<SharingInteractor> {
        SharingInteractorImpl(repository =  get())
    }
    factory<SearchTrackInteracktor> {
        SearchTrackInteractorImpl(repository =  get())
    }
    factory<PlayerInteractor> {
        PlayerInteractorImpl()
    }

}