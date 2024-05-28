package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.domain.repository.PlayerRepository


class App : Application() {
    companion object {
        const val KEY_THEME_SWITCHER = "key for switcher"
        internal lateinit var INSTANCE: App
            private set
    }

    var darkTheme = false
    private lateinit var sharedPrefs : SharedPreferences
    fun getRepository() : PlayerRepository = PlayerRepositoryImpl()

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this


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

