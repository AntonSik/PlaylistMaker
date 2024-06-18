package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.EmailData


interface SharingInteractor {
    fun getUrl(appLinkId: Int): String
    fun writeToSupport(recipientId: Int, subjectId : Int, textId: Int ): EmailData
}