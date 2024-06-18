package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.storage.ThemeStorage

interface SettingsRepository {
    fun getThemeStorage(): ThemeStorage
    fun getTheme(themeStorage: ThemeStorage): Boolean
    fun updateThemeStorage(themeStorage: ThemeStorage, theme: Boolean)

}