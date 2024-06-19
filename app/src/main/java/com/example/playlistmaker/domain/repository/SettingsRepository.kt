package com.example.playlistmaker.domain.repository


interface SettingsRepository {

    fun getTheme(): Boolean
    fun updateThemeStorage(theme: Boolean)

}