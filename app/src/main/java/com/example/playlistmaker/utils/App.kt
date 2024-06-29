package com.example.playlistmaker.utils

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {
    companion object {
        const val KEY_THEME_SWITCHER = "key for switcher"
        const val SHARED_PREFERENCES = "Shared pref's key"
    }

    var darkTheme = false

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(interactorModule, repositoryModule, dataModule, viewModelModule)
        }


        sharedPrefs = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
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

