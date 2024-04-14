package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var coverTrack: ImageView
    private lateinit var nameTrack: TextView
    private lateinit var artistsTrack: TextView
    private lateinit var durationTrack: TextView
    private lateinit var albumTrack: TextView
    private lateinit var yearTrack: TextView
    private lateinit var genreTrack: TextView
    private lateinit var countryTrack: TextView
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val selectedTrack = intent.getParcelableExtra<Track>("clicked track")

        val imageArrow = findViewById<ImageButton>(R.id.ib_arrowBack)
        coverTrack = findViewById(R.id.iv_cover)
        nameTrack = findViewById(R.id.tv_trackName)
        artistsTrack = findViewById(R.id.tv_artists)
        durationTrack = findViewById(R.id.tv_enterDuration)
        albumTrack = findViewById(R.id.tv_enterAlbum)
        yearTrack = findViewById(R.id.tv_enterYear)
        genreTrack = findViewById(R.id.tv_enterGenre)
        countryTrack = findViewById(R.id.tv_enterCountry)
        bind(selectedTrack)

        imageArrow.setOnClickListener { finish() }


    }

    fun bind(track: Track?) {
        val url = track?.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f, this)))
            .into(coverTrack)
        nameTrack.text = track?.trackName
        artistsTrack.text = track?.artistName
        durationTrack.text = dateFormat.format(track?.trackTimeMillis)
        albumTrack.text = track?.collectionName
        genreTrack.text = track?.primaryGenreName
        yearTrack.text = track?.releaseData
        countryTrack.text =track?.country
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}