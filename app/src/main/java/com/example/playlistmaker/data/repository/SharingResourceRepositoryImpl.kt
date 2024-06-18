package com.example.playlistmaker.data.repository

import android.content.Context
import com.example.playlistmaker.data.storage.ThemeStorage
import com.example.playlistmaker.domain.repository.SharingResourceRepository

class SharingResourceRepositoryImpl(val context: Context):SharingResourceRepository {
    override fun getString(id: Int): String {
        val resource = context.getString(id)
        return resource
    }
}