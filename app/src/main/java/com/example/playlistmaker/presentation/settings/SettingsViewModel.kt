package com.example.playlistmaker.presentation.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.models.EmailData
import com.example.playlistmaker.utils.App
import com.example.playlistmaker.utils.Creator

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val sharingInteractor: SharingInteractor =
        Creator.provideSharingInteractor(getApplication())
    private val settingsRepository = Creator.provideSettingsRepository(getApplication())
    private val storage = settingsRepository.getThemeStorage()


    fun getTheme(): Boolean {
        return settingsRepository.getTheme(storage)
    }

    fun changeTheme(checked: Boolean) {
        (getApplication<Application>() as App).switchTheme(checked)
        settingsRepository.updateThemeStorage(storage, checked)
    }

    fun shareApp(): String {
        return sharingInteractor.getUrl(R.string.shareMessage)
    }

    fun writeToSupport(): EmailData {
        val supportIntent = sharingInteractor.writeToSupport(
            recipientId = R.string.myEmail,
            subjectId = R.string.themeofMessage,
            textId = R.string.message
        )
        return supportIntent
    }

    fun openTerms(): String {
        return sharingInteractor.getUrl(R.string.urlOfferta)
    }
}