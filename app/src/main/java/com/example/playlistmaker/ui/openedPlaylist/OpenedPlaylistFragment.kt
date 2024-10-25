package com.example.playlistmaker.ui.openedPlaylist

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentOpenedPlaylistBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.openedPlaylist.OpenedPlaylistViewModel
import com.example.playlistmaker.ui.audioPlayer.AudioPlayerActivity
import com.example.playlistmaker.ui.openedPlaylist.models.PlaylistScreenState
import com.example.playlistmaker.ui.openedPlaylist.models.PlaylistTracksState
import com.example.playlistmaker.ui.playlistRedactor.PlaylistRedactorFragment
import com.example.playlistmaker.ui.root.BottomNavBarShower
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class OpenedPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = OpenedPlaylistFragment()
        private const val CLICK_DEBOUNCE_DELAY = 2000L
        private const val CLICKED_ITEM = "clicked track"
        const val CLICKED_PLAYLIST_ID = "selectedPlaylist"

    }

    private lateinit var binding: FragmentOpenedPlaylistBinding
    private lateinit var viewModel: OpenedPlaylistViewModel
    private lateinit var confirmDeleteTrackDialog: MaterialAlertDialogBuilder
    private lateinit var confirmDeletePlaylistDialog: MaterialAlertDialogBuilder
    private var adapter: TrackAdapter? = null
    private var isClickAllowed = true
    private val dateFormat by lazy { SimpleDateFormat("mm", Locale.getDefault()) }
    private var selectedTrackId: Int? = null
    private lateinit var currentPlaylistTitle: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpenedPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedPlaylistId: Int = arguments?.getInt(CLICKED_PLAYLIST_ID) ?: return

        viewModel = getViewModel { parametersOf(selectedPlaylistId) }

        init()

        viewModel.screenStateLive.observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlaylistScreenState.Content -> {
                    changeContentVisibility(loading = false)

                    val url = screenState.playlistModel.filePath

                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .fitCenter()
                        .into(binding.ivCover)

                    binding.tvPlaylistTitle.text = screenState.playlistModel.title
                    currentPlaylistTitle = screenState.playlistModel.title
                    binding.tvDescription.text = screenState.playlistModel.description
                    binding.tvCountTracks.text =
                        changingTracksCountEnding(screenState.playlistModel.trackCount)


                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .transform(
                            CenterCrop(), RoundedCorners(dpToPx(2f, requireContext()))
                        )
                        .into(binding.itemPlaylist.ivCoverIcon)

                    binding.itemPlaylist.tvPlaylistTitle.text = screenState.playlistModel.title
                    setUpDeletePlaylistDialog()
                }

                PlaylistScreenState.Loading -> {
                    changeContentVisibility(loading = true)
                }
            }
        }

        viewModel.playlistTracksLive.observe(viewLifecycleOwner) {
            render(it)

        }
        viewModel.minutesOfAllTracksLive.observe(viewLifecycleOwner) {
            binding.tvCountMinutes.text = changingMinutesDurationEnding(it)
        }
        viewModel.countOfAllTracksLive.observe(viewLifecycleOwner) {
            binding.tvCountTracks.text = changingTracksCountEnding(it)
            binding.itemPlaylist.tvPlaylistCountTracks.text = changingTracksCountEnding(it)
        }
        val bottomSheetTracksContainer: LinearLayout = binding.tracksHolder
        val bottomSheetTracksBehavior = BottomSheetBehavior.from(bottomSheetTracksContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetTracksBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val params = bottomSheet.layoutParams as ViewGroup.MarginLayoutParams
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.visibility = View.GONE
                        params.topMargin = 64
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        params.topMargin = 0
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffSet: Float) {
                binding.overlay.alpha = slideOffSet
            }

        })
        val bottomSheetMenuContainer: LinearLayout = binding.menuBottomSheet
        val bottomSheetMenuBehavior = BottomSheetBehavior.from(bottomSheetMenuContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetMenuBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.visibility = View.GONE

                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE

                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffSet: Float) {
                binding.overlay.alpha = slideOffSet
                binding.tracksHolder.isVisible = slideOffSet < 0
            }

        })


        binding.ibArrowBack.setOnClickListener {
            navigateBack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateBack()
        }
        binding.ivShare.setOnClickListener {
            if (clickDebounce()) {
                setupSharing()
            }
        }
        binding.ivMenu.setOnClickListener {

            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        }
        binding.tvBottomShare.setOnClickListener {
            if (clickDebounce()) {
                setupSharing()
            }
        }
        binding.tvBottomRedactor.setOnClickListener {

            val fragment = PlaylistRedactorFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putInt(CLICKED_PLAYLIST_ID, selectedPlaylistId)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.rootFragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.tvBottomDelete.setOnClickListener { confirmDeletePlaylistDialog.show() }
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter = null
        binding.rvRecyclerViewTracksHolder.adapter = null
    }

    private fun init() {
        adapter = TrackAdapter(object : TrackAdapter.OnClickListenerItem {

            override fun onItemClick(track: Track) {
                if (clickDebounce()) {
                    val playerIntent = Intent(requireContext(), AudioPlayerActivity::class.java)
                    playerIntent.putExtra(CLICKED_ITEM, track)
                    startActivity(playerIntent)
                }
            }
        }, object : TrackAdapter.OnLongClickListenerItem {
            override fun onItemLongClick(track: Track): Boolean {
                selectedTrackId = track.trackId
                confirmDeleteTrackDialog.show()
                return true
            }

        })
        binding.rvRecyclerViewTracksHolder.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvRecyclerViewTracksHolder.adapter = adapter
        viewModel.loadPlaylistData()
        setUpDeleteTrackDialog()

    }

    private fun setUpDeleteTrackDialog() {

        confirmDeleteTrackDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
                .setTitle(R.string.confirm_delete_track)
                .setNegativeButton(R.string.dialog_deleting_negative) { dialog, _ ->
                    dialog.dismiss()

                }
                .setPositiveButton(R.string.dialog_deleting_positive) { _, _ ->
                    selectedTrackId?.let { trackId ->
                        viewModel.deleteTrack(trackId)
                    }

                }

    }

    private fun setUpDeletePlaylistDialog() {

        confirmDeletePlaylistDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
                .setTitle(
                    requireContext().getString(
                        R.string.playlist_delete_dialog,
                        binding.tvPlaylistTitle.text
                    )
                )
                .setNegativeButton(R.string.dialog_deleting_negative) { dialog, _ ->
                    dialog.dismiss()

                }
                .setPositiveButton(R.string.dialog_deleting_positive) { _, _ ->
                    viewModel.deletePlaylist()
                    navigateBack()
                }

    }

    private fun changeContentVisibility(loading: Boolean) {
        binding.progressBarPlaylist.isVisible = loading

        binding.ivCover.isVisible = !loading
        binding.tvPlaylistTitle.isVisible = !loading
        binding.tvDescription.isVisible = !loading
        binding.tvCountMinutes.isVisible = !loading
        binding.tvCountTracks.isVisible = !loading
        binding.rvRecyclerViewTracksHolder.isVisible = !loading

    }

    private fun changingTracksCountEnding(tracksCount: Int?): String {
        val trackEnding = requireContext().resources
        val defaultEnding = requireContext().getString(R.string.zero_tracks)
        return if (tracksCount == null) {
            defaultEnding
        } else {
            return trackEnding.getQuantityString(
                R.plurals.count_of_tracks,
                tracksCount,
                tracksCount
            )
        }
    }

    private fun changingMinutesDurationEnding(sumMinutes: Long?): String {

        val minutesEnding = requireContext().resources
        val defaultEnding = requireContext().getString(R.string.zero_minutes)
        return if (sumMinutes == null) {
            defaultEnding

        } else {
            val formattedMinutes = dateFormat.format(sumMinutes).toInt()
            return minutesEnding.getQuantityString(
                R.plurals.ending_of_minutes,
                formattedMinutes,
                formattedMinutes
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun render(state: PlaylistTracksState) {
        when (state) {
            is PlaylistTracksState.Content -> showContent(state.trackList)
            is PlaylistTracksState.Empty -> showEmpty(state.message)
            is PlaylistTracksState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        binding.rvRecyclerViewTracksHolder.visibility = View.GONE
        binding.tvBottomSheetPlaceholderMessage.visibility = View.GONE
        binding.ivBottomSheetPlaceholderImage.visibility = View.GONE
        binding.bottomSheetProgressBar.visibility = View.VISIBLE
    }

    private fun showEmpty(message: String) {
        binding.rvRecyclerViewTracksHolder.visibility = View.GONE
        binding.tvBottomSheetPlaceholderMessage.visibility = View.VISIBLE
        binding.ivBottomSheetPlaceholderImage.visibility = View.VISIBLE
        binding.bottomSheetProgressBar.visibility = View.GONE

        binding.tvBottomSheetPlaceholderMessage.text = message
    }

    private fun showContent(tracks: List<Track>) {
        binding.rvRecyclerViewTracksHolder.visibility = View.VISIBLE
        binding.tvBottomSheetPlaceholderMessage.visibility = View.GONE
        binding.ivBottomSheetPlaceholderImage.visibility = View.GONE
        binding.bottomSheetProgressBar.visibility = View.GONE

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun setupSharing() {
        if ((viewModel.playlistTracksLive.value is PlaylistTracksState.Empty)) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.empty_tracks_playlist_message),
                Toast.LENGTH_LONG
            ).show()
        } else {

            val shareText = viewModel.sharePlaylistText()
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            shareIntent.setType("text/plain")
            startActivity(shareIntent)
        }
    }

    private fun navigateBack() {
        requireActivity().supportFragmentManager.popBackStack()
        (activity as? BottomNavBarShower)?.showNavbar()
    }
}