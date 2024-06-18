package com.example.playlistmaker.domain.repository

interface SharingResourceRepository {
    fun getString(id: Int): String
}