package com.example.playlistmaker.data.repository

import android.content.Context
import com.example.playlistmaker.data.storage.ThemeStorage
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.utils.App.Companion.SHARED_PREFERENCES

class SettingsRepositoryImpl(

    val context: Context
):SettingsRepository {
    override fun getThemeStorage(): ThemeStorage {
      return ThemeStorage(context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE))
    }

    override fun getTheme(themeStorage: ThemeStorage): Boolean {
        return themeStorage.getTheme()
    }

    override fun updateThemeStorage(themeStorage: ThemeStorage, theme: Boolean) {
        themeStorage.updateTheme(theme)
    }

}