package com.example.playlistmaker.ui.audioPlayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.audioPlayer.AudioPlayerViewModel
import com.example.playlistmaker.ui.audioPlayer.models.PlayerScreenState
import com.example.playlistmaker.ui.audioPlayer.models.PlayerState
import com.example.playlistmaker.ui.media.playlistCreator.PlaylistsCreatorFragment
import com.example.playlistmaker.ui.root.BottomNavBarShower
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {
    companion object {
        fun newInstance() = AudioPlayerFragment()
        private const val CLICKED_ITEM = "clicked track"
    }

    private lateinit var binding: FragmentAudioPlayerBinding
    private lateinit var viewModel: AudioPlayerViewModel
    private var adapter: BottomSheetPlaylistAdapter? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? BottomNavBarShower)?.hideNavBar()
        val selectedTrack = arguments?.getParcelable<Track>(CLICKED_ITEM)!!
        viewModel = getViewModel { parametersOf(selectedTrack) }

        val bottomSheetContainer = binding.playlistsBottomSheet
        val overlay = binding.overlay

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        overlay.visibility = View.VISIBLE
                        overlay.alpha = 0.5f
                        viewModel.playlistsLive.observe(viewLifecycleOwner) { playlists ->

                            showContent(playlists)
                        }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                        viewModel.playlistsLive.observe(viewLifecycleOwner) { playlists ->

                            showContent(playlists)
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = slideOffset
            }
        })

        adapter = BottomSheetPlaylistAdapter { playlist ->
            viewModel.onInsertTrackToPlaylist(
                playlist
            )
        }
        binding.rvBottomSheetRecyclerView.adapter = adapter
        viewModel.fillBottomSheet()
        viewModel.playlistsLive.observe(viewLifecycleOwner) { playlists ->

            showContent(playlists)
        }

        viewModel.insertTrackStatusLive.observe(viewLifecycleOwner) { insertTrackStatus ->

            if (insertTrackStatus.isSuccess) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.track_added_successfully, insertTrackStatus.playlistTitle),
                    Toast.LENGTH_LONG
                ).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.track_already_added, insertTrackStatus.playlistTitle),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.screenStateLive.observe(viewLifecycleOwner) { screenState ->
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
                        .transform(
                            FitCenter(), RoundedCorners(dpToPx(8f, requireActivity()))
                        )
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

                    if (screenState.playerModel.isFavorite) {
                        binding.btnLike.setImageResource(R.drawable.heart_active)
                    } else {
                        binding.btnLike.setImageResource(R.drawable.heart_vector)
                    }
                }

                is PlayerScreenState.Loading -> {
                    changeContentVisibility(loading = true)
                }
            }
        }

        binding.btnPlay.setOnClickListener {
            viewModel.playBackControl()
        }
        binding.ibArrowBack.setOnClickListener { navigateBack() }

        binding.btnLike.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.btnAddMedia.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnBottomSheetAddNewPlaylist.setOnClickListener {

            val fragment = PlaylistsCreatorFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.rootFragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }


        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.btnPlay.isEnabled = it.isPlayButtonEnabled
            binding.tvTimePlaying.text = it.progress
            changePlayButtonStyle(it.buttonType)
        }
        viewModel.favoriteLive.observe(viewLifecycleOwner) { isFavorite ->
            changeLikeButtonStyle(isFavorite)
        }
    }

    override fun onResume() {
        super.onResume()

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        viewModel
        if (viewModel.favoriteLive.value == true) {
            changeLikeButtonStyle(true)
        }
    }


    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (viewModel.observePlayerState().value is PlayerState.Playing) {
            viewModel.onPause()
            changePlayButtonStyle(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter = null
        binding.rvBottomSheetRecyclerView.adapter = null
    }


    private fun changePlayButtonStyle(playingStatus: Boolean) {
        when (playingStatus) {
            true -> binding.btnPlay.setImageResource(R.drawable.pause_vector)
            else -> binding.btnPlay.setImageResource(R.drawable.play_vector)
        }
    }

    private fun changeLikeButtonStyle(isFavorite: Boolean) {
        when (isFavorite) {
            true -> binding.btnLike.setImageResource(R.drawable.heart_active)
            else -> binding.btnLike.setImageResource(R.drawable.heart_vector)
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

    private fun showContent(playlists: List<Playlist>) {

        binding.rvBottomSheetRecyclerView.visibility = View.VISIBLE
        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
    }


    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun navigateBack() {
        requireActivity().supportFragmentManager.popBackStack()
        (activity as? BottomNavBarShower)?.showNavbar()
    }

}