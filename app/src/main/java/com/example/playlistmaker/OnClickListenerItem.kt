package com.example.playlistmaker

import com.example.playlistmaker.domain.models.Track


interface OnClickListenerItem {
    fun onItemClick(track: Track)
}