package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.models.EmailData
import com.example.playlistmaker.domain.repository.SharingResourceRepository

class SharingInteractorImpl(
    private val stringsRepository: SharingResourceRepository,

):SharingInteractor {
    override fun getUrl(appLinkId: Int): String {
      return  stringsRepository.getString(appLinkId)
    }

    override fun writeToSupport(recipienId: Int, subjectId: Int, textId: Int): EmailData {
        return EmailData(
            recipient = stringsRepository.getString(recipienId),
            subject = stringsRepository.getString(subjectId),
            text = stringsRepository.getString(textId)
        )
    }
}