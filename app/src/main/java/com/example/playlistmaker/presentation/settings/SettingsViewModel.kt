package com.example.playlistmaker.presentation.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.models.EmailData
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.utils.App

class SettingsViewModel(
    application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsRepository: SettingsRepository,

    ) : AndroidViewModel(application) {


    fun getTheme(): Boolean {
        return settingsRepository.getTheme()
    }

    fun changeTheme(checked: Boolean) {
        (getApplication<Application>() as App).switchTheme(checked)
        settingsRepository.updateThemeStorage(checked)
    }

    fun shareApp(): String {
        return sharingInteractor.getUrl(R.string.shareMessage)
    }

    fun writeToSupport(): EmailData {
        return sharingInteractor.writeToSupport(
            recipientId = R.string.myEmail,
            subjectId = R.string.themeofMessage,
            textId = R.string.message
        )
    }

    fun openTerms(): String {
        return sharingInteractor.getUrl(R.string.urlOfferta)
    }
}