package com.example.playlistmaker

import android.app.Application
import android.content.pm.PackageManager.ComponentEnabledSetting
import androidx.appcompat.app.AppCompatDelegate

const val SHARED_PREF_NAME = "shared prefs"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean("darkTheme",false)
    }

    fun switchTheme(darkThemeEnabled:Boolean){
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled){
                AppCompatDelegate.MODE_NIGHT_YES
            }else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}