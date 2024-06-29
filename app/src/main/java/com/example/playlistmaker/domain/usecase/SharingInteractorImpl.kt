package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.models.EmailData
import com.example.playlistmaker.domain.repository.SharingResourceRepository

class SharingInteractorImpl(private val repository: SharingResourceRepository) : SharingInteractor {
    override fun getUrl(appLinkId: Int): String {
        return repository.getString(appLinkId)
    }

    override fun writeToSupport(recipientId: Int, subjectId: Int, textId: Int): EmailData {
        return EmailData(
            recipient = repository.getString(recipientId),
            subject = repository.getString(subjectId),
            text = repository.getString(textId)
        )
    }
}