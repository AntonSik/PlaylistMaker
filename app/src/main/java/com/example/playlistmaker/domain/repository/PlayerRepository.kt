package com.example.playlistmaker.domain.repository

import android.widget.ImageButton
import android.widget.TextView

interface PlayerRepository {

    fun startPlayer(playBtn: ImageButton)

    fun pausePlayer(playBtn: ImageButton)

    fun preparePlayer(playBtn: ImageButton, timePlaying: TextView, recordsUrl: String?)

    fun playBackControl(playBtn: ImageButton, timePlaying: TextView)

    fun createPlayingTimer(startTime: Long, timePlaying: TextView): Runnable
}