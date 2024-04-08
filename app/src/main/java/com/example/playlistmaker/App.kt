package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    companion object {
        const val KEY_THEME_SWITCHER = "key for switcher"

    }

    var darkTheme = false
    private lateinit var sharedPrefs : SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(HistoryPrefs.SHARED_PREFERENCES_HISTORY, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(
            KEY_THEME_SWITCHER,
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        )

        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit().putBoolean(KEY_THEME_SWITCHER, darkTheme).apply()
    }
}

