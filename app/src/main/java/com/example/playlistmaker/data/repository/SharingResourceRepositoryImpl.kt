package com.example.playlistmaker.data.repository

import android.content.Context
import com.example.playlistmaker.domain.repository.SharingResourceRepository

class SharingResourceRepositoryImpl(private val context: Context) : SharingResourceRepository {
    override fun getString(id: Int): String {
        return context.getString(id)
    }
}