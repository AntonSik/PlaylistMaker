package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.utils.App


class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    val context: Context
) : SettingsRepository {

    override fun getTheme(): Boolean {
         return sharedPrefs.getBoolean(App.KEY_THEME_SWITCHER, false)
    }

    override fun updateThemeStorage( theme: Boolean) {
        sharedPrefs
            .edit()
            .putBoolean(App.KEY_THEME_SWITCHER, theme)
            .apply()
    }

}