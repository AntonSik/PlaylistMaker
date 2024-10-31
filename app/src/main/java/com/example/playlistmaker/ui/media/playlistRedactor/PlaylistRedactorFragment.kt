package com.example.playlistmaker.ui.media.playlistRedactor

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.PlaylistModel
import com.example.playlistmaker.presentation.media.PlaylistsCreatorViewModel
import com.example.playlistmaker.ui.media.playlistCreator.PlaylistsCreatorFragment
import com.example.playlistmaker.ui.root.BottomNavBarShower
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistRedactorFragment : PlaylistsCreatorFragment() {

    companion object {
        fun newInstance() = PlaylistRedactorFragment()
        private const val CLICKED_PLAYLIST_ID = "selectedPlaylist"
    }


    private val viewModel by viewModel<PlaylistsCreatorViewModel>()
    private var currentPlaylist: PlaylistModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BottomNavBarShower)?.hideNavBar()
        binding.tvHeader.text = getString(R.string.redacting_playlist_header)
        binding.createPlaylistBtn.text = getString(R.string.redactor_playlist_save)

        val selectedPlaylistId: Int = arguments?.getInt(CLICKED_PLAYLIST_ID) ?: return
        viewModel.loadPlaylist(selectedPlaylistId)

        viewModel.playlistLive.observe(viewLifecycleOwner) { playlistModel ->
            currentPlaylist = playlistModel
            val url = playlistModel.filePath
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .transform(
                    CenterCrop(), RoundedCorners(dpToPx(2f, requireContext()))
                )
                .into(binding.ivPickerCover)

            binding.etTitle.setText(playlistModel.title)
            binding.etDescription.setText(playlistModel.description)
        }

        binding.createPlaylistBtn.setOnClickListener {
            updatePlaylist(currentPlaylist!!)
            navigateBack()

        }

        binding.ibBack.setOnClickListener {
            navigateBack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateBack()
        }
    }

    private fun updatePlaylist(playlistModel: PlaylistModel) {
        val filePath = loadImage() ?: playlistModel.filePath
        val updatedPlaylist = Playlist(
            playlistId = playlistModel.playlistId,
            title = binding.etTitle.text.toString(),
            description = binding.etDescription.text.toString(),
            filePath = filePath,
            trackIds = playlistModel.trackIds,
            trackCount = playlistModel.trackCount
        )
        viewModel.updatePlaylist(updatedPlaylist)

    }

    private fun navigateBack() {
        requireActivity().supportFragmentManager.popBackStack()

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}