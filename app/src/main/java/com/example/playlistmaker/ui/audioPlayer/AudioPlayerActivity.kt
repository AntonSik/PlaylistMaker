package com.example.playlistmaker.ui.audioPlayer

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.audioPlayer.AudioPlayerViewModel
import com.example.playlistmaker.ui.audioPlayer.models.PlayerScreenState
import com.example.playlistmaker.ui.audioPlayer.models.PlayerState
import com.example.playlistmaker.ui.search.SearchFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: AudioPlayerViewModel
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedTrack = intent.getParcelableExtra<Track>(SearchFragment.CLICKED_ITEM)!!
        viewModel = getViewModel { parametersOf(selectedTrack) }

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.screenStateLive.observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    changeContentVisibility(loading = false)

                    val url = screenState.playerModel.artworkUrl100?.replaceAfterLast(
                        '/',
                        "512x512bb.jpg"
                    )
                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .fitCenter()
                        .transform(RoundedCorners(dpToPx(8f, this)))
                        .into(binding.ivCover)

                    binding.tvTrackName.text = screenState.playerModel.trackName
                    binding.tvArtists.text = screenState.playerModel.artistName
                    binding.tvEnterDuration.text =
                        dateFormat.format(screenState.playerModel.trackTimeMillis)

                    if (screenState.playerModel.collectionName.isNullOrEmpty()) {
                        binding.tvEnterAlbum.visibility = View.GONE
                        binding.tvAlbum.visibility = View.GONE
                    } else {
                        binding.tvEnterAlbum.text = screenState.playerModel.collectionName
                        binding.tvEnterAlbum.visibility = View.VISIBLE
                        binding.tvAlbum.visibility = View.VISIBLE
                    }

                    if (screenState.playerModel.releaseDate.isNullOrEmpty()) {
                        binding.tvEnterYear.visibility = View.GONE
                        binding.tvYear.visibility = View.GONE
                    } else {
                        if (screenState.playerModel.releaseDate.length > 4) {
                            binding.tvEnterYear.text =
                                screenState.playerModel.releaseDate.substring(0, 4)
                        } else {
                            binding.tvEnterYear.text = screenState.playerModel.releaseDate
                        }
                        binding.tvEnterYear.visibility = View.VISIBLE
                        binding.tvYear.visibility = View.VISIBLE
                    }
                    binding.tvEnterGenre.text = screenState.playerModel.primaryGenreName
                    binding.tvEnterCountry.text = screenState.playerModel.country
                }

                is PlayerScreenState.Loading -> {
                    changeContentVisibility(loading = true)
                }
            }
        }

        binding.btnPlay.setOnClickListener {
            viewModel.playBackControl()
        }
        binding.ibArrowBack.setOnClickListener { finish() }

        viewModel.observePlayerState().observe(this) {
            binding.btnPlay.isEnabled = it.isPlayButtonEnabled
            binding.tvTimePlaying.text = it.progress
            changeButtonStyle(it.buttonType)
        }
    }


    override fun onPause() {
        super.onPause()
        if (viewModel.observePlayerState().value is PlayerState.Playing) {
            viewModel.onPause()
            changeButtonStyle(false)
        }
    }


    private fun changeButtonStyle(playingStatus: Boolean) {
        when (playingStatus) {
            true -> binding.btnPlay.setImageResource(R.drawable.pause_vector)
            else -> binding.btnPlay.setImageResource(R.drawable.play_vector)
        }
    }

    private fun changeContentVisibility(loading: Boolean) {
        binding.progressBarPlayer.isVisible = loading

        binding.btnPlay.isVisible = !loading
        binding.ivCover.isVisible = !loading
        binding.tvTrackName.isVisible = !loading
        binding.tvArtists.isVisible = !loading
        binding.tvDuration.isVisible = !loading
        binding.tvEnterAlbum.isVisible = !loading
        binding.tvAlbum.isVisible = !loading
        binding.tvEnterYear.isVisible = !loading
        binding.tvYear.isVisible = !loading
        binding.tvEnterGenre.isVisible = !loading
        binding.tvEnterCountry.isVisible = !loading
        binding.tvTimePlaying.isVisible = !loading
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}