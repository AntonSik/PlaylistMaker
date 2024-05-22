package com.example.playlistmaker.presentation.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.CLICKED_ITEM
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.AudioPlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        private const val UPDATING_DELAY = 400L
    }

    val playerRepository by lazy { App.INSTANCE.playerRepository}
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

    //private lateinit var  playerRepository : PlayerRepository
    val handler = Handler(Looper.getMainLooper())
    val mediaPlayer = MediaPlayer()
    var playerState = AudioPlayerState.DEFAULT


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        //    playerRepository = App.INSTANCE.playerRepository
        val selectedTrack = intent.getParcelableExtra<Track>(CLICKED_ITEM)

        val imageArrow = findViewById<ImageButton>(R.id.ib_arrowBack)
        init()
        bind(selectedTrack)
        playerRepository.preparePlayer(recordsUrl)

        playBtn.setOnClickListener {
            playBackControl(playerState)
        }

        imageArrow.setOnClickListener { finish() }

        playerRepository.setOnCompletionCallBack {
            playBtn.isEnabled = true
            timePlaying.text = getString(R.string.zero)
            playBtn.setImageResource(R.drawable.play_vector)
            handler.removeCallbacksAndMessages(null)
        }

    }

    override fun onPause() {
        super.onPause()
        playerRepository.pausePlayer{playBtn.setImageResource(R.drawable.play_vector)}
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    private fun playBackControl(state : AudioPlayerState) {
        when (state) {
            AudioPlayerState.PLAYING -> {
                playerRepository.pausePlayer{playBtn.setImageResource(R.drawable.play_vector)}
                handler.removeCallbacksAndMessages(null)
            }

            AudioPlayerState.PREPARED, AudioPlayerState.PAUSED -> {

                playerRepository.startPlayer{
                    playBtn.setImageResource(R.drawable.pause_vector)

                }
                val startPlaying = System.currentTimeMillis()   //время начала отсчета
                handler.post(createPlayingTimer(startPlaying))
            }
            AudioPlayerState.DEFAULT ->{
                playerRepository.preparePlayer(recordsUrl)
            }
        }
    }
    private fun createPlayingTimer(startTime: Long): Runnable {
        return object : Runnable {
            override fun run() {

                val countedTime = startTime + mediaPlayer.currentPosition

                if (countedTime > 0) {
                    if (playerState == AudioPlayerState.PLAYING) {
                        timePlaying.text = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                        handler.postDelayed(this, UPDATING_DELAY)
                    }
                }

            }
        }
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