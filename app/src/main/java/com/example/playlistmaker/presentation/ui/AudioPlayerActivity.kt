package com.example.playlistmaker.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.CLICKED_ITEM
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private val playerRepository by lazy { PlayerRepositoryImpl(context = applicationContext) }
    private lateinit var coverTrack: ImageView
    private lateinit var nameTrack: TextView
    private lateinit var artistsTrack: TextView
    private lateinit var durationTrack: TextView
    private lateinit var albumTrack: TextView
    private lateinit var album: TextView
    private lateinit var yearTrack: TextView
    private lateinit var year: TextView
    private lateinit var genreTrack: TextView
    private lateinit var countryTrack: TextView
    private lateinit var timePlaying: TextView
    private lateinit var playBtn: ImageButton
    private var recordsUrl: String? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val selectedTrack = intent.getParcelableExtra<Track>(CLICKED_ITEM)

        val imageArrow = findViewById<ImageButton>(R.id.ib_arrowBack)
        init()
        bind(selectedTrack)
        playerRepository.preparePlayer(playBtn, timePlaying, recordsUrl)

        playBtn.setOnClickListener {
            playerRepository.playBackControl(playBtn, timePlaying)
        }

        imageArrow.setOnClickListener { finish() }

    }

    override fun onPause() {
        super.onPause()
        playerRepository.pausePlayer(playBtn)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerRepository.mediaPlayer.release()
        playerRepository.handler.removeCallbacksAndMessages(null)
    }

    private fun bind(track: Track?) {
        val url = track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(coverTrack)
        nameTrack.text = track?.trackName
        artistsTrack.text = track?.artistName
        durationTrack.text = dateFormat.format(track?.trackTimeMillis)
        if (track?.collectionName.isNullOrEmpty()) {
            albumTrack.visibility = View.GONE
            album.visibility = View.GONE
        } else {
            albumTrack.text = track?.collectionName
            albumTrack.visibility = View.VISIBLE
            album.visibility = View.VISIBLE
        }
        genreTrack.text = track?.primaryGenreName
        if (track?.releaseDate.isNullOrEmpty()) {
            yearTrack.visibility = View.GONE
            year.visibility = View.GONE
        } else {
            if (track?.releaseDate?.length!! > 4) yearTrack.text = track.releaseDate.substring(0, 4)
            yearTrack.visibility = View.VISIBLE
            year.visibility = View.VISIBLE
        }
        countryTrack.text = track?.country
        recordsUrl = track?.previewUrl
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun init() {
        coverTrack = findViewById(R.id.iv_cover)
        nameTrack = findViewById(R.id.tv_trackName)
        artistsTrack = findViewById(R.id.tv_artists)
        durationTrack = findViewById(R.id.tv_enterDuration)
        albumTrack = findViewById(R.id.tv_enterAlbum)
        album = findViewById(R.id.tv_album)
        yearTrack = findViewById(R.id.tv_enterYear)
        year = findViewById(R.id.tv_year)
        genreTrack = findViewById(R.id.tv_enterGenre)
        countryTrack = findViewById(R.id.tv_enterCountry)
        timePlaying = findViewById(R.id.tv_timePlaying)
        playBtn = findViewById(R.id.btn_play)
    }

}