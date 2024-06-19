package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.utils.App.Companion.KEY_THEME_SWITCHER

class ThemeStorage(private val sharedPrefs: SharedPreferences) {


    fun getTheme(): Boolean {
        return sharedPrefs.getBoolean(KEY_THEME_SWITCHER, false)
    }

    fun updateTheme(theme: Boolean) {
        sharedPrefs
            .edit()
            .putBoolean(KEY_THEME_SWITCHER, theme)
            .apply()
    }


}