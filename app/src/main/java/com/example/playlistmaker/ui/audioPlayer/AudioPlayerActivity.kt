package com.example.playlistmaker.ui.audioPlayer

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.audioPlayer.AudioPlayerViewModel
import com.example.playlistmaker.ui.audioPlayer.models.PlayerScreenState
import com.example.playlistmaker.ui.search.SearchActivity
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel : AudioPlayerViewModel
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedTrack = intent.getParcelableExtra<Track>(SearchActivity.CLICKED_ITEM)

        viewModel = ViewModelProvider(
            this,
            AudioPlayerViewModel.getViewModelFactory(selectedTrack)
        )[AudioPlayerViewModel::class.java]

        viewModel.screenStateLive.observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    changeContentVisibility(loading = false)

                    val url = screenState.PlayerModel.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .fitCenter()
                        .transform(RoundedCorners(dpToPx(8f, this)))
                        .into(binding.ivCover)

                    binding.tvTrackName.text = screenState.PlayerModel.trackName
                    binding.tvArtists.text = screenState.PlayerModel.artistName
                    binding.tvEnterDuration.text = dateFormat.format(screenState.PlayerModel.trackTimeMillis)

                    if (screenState.PlayerModel.collectionName.isNullOrEmpty()) {
                        binding.tvEnterAlbum.visibility = View.GONE
                        binding.tvAlbum.visibility = View.GONE
                    } else {
                        binding.tvEnterAlbum.text = screenState.PlayerModel.collectionName
                        binding.tvEnterAlbum.visibility = View.VISIBLE
                        binding.tvAlbum.visibility = View.VISIBLE
                    }

                    if (screenState.PlayerModel.releaseDate.isNullOrEmpty()) {
                        binding.tvEnterYear.visibility = View.GONE
                        binding.tvYear.visibility = View.GONE
                    } else {
                        if (screenState.PlayerModel.releaseDate.length > 4) {
                            binding.tvEnterYear.text = screenState.PlayerModel.releaseDate.substring(0, 4)
                        }else {
                            binding.tvEnterYear.text = screenState.PlayerModel.releaseDate
                        }
                        binding.tvEnterYear.visibility = View.VISIBLE
                        binding.tvYear.visibility = View.VISIBLE
                    }
                    binding.tvEnterGenre.text = screenState.PlayerModel.primaryGenreName
                    binding.tvEnterCountry.text = screenState.PlayerModel.country
                }

                is PlayerScreenState.Loading -> {
                    changeContentVisibility(loading = true)
                }
            }
        }
        viewModel.statusLive.observe(this) { status ->
            when (status.isPlaying) {
                true -> {
                    changeButtonStyle(status.isPlaying)
                }

                else -> {
                    changeButtonStyle(status.isPlaying)
                }
            }
            if (status.isCompleted)
                binding.tvTimePlaying.text = getString(R.string.zero)
        }
        viewModel.timerLive.observe(this){ timer ->
            binding.tvTimePlaying.text = timer
        }
        binding.btnPlay.setOnClickListener {
            viewModel.playBackControl()
        }
        binding.ibArrowBack.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
        changeButtonStyle(false)

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
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