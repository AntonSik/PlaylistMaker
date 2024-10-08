package com.example.playlistmaker.ui.media.playlists

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsCreatorBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.media.PlaylistsCreatorViewModel
import com.example.playlistmaker.ui.root.BottomNavBarShower
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistsCreatorFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistsCreatorBinding
    private lateinit var confirmCloseCreatingDialog: MaterialAlertDialogBuilder
    private val viewModel by viewModel<PlaylistsCreatorViewModel>()
    private var isCreateAllowed = false
    private var isImageSelected = false
    private val requester = PermissionRequester.instance()


    companion object {
        fun newInstance() = PlaylistsCreatorFragment()
        const val PREVIOUS_SCREEN = "previous screen"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDialog()

        val pickPhoto =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.ivPickerCover.setImageURI(uri)
                    val imagePath = chooseImageAndSave(uri)
                    viewModel.writeFileName(imagePath)
                    isImageSelected = true
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.createPlaylistBtn.isEnabled = isCreateAllowed

        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isCreateAllowed = !s.isNullOrEmpty()
                binding.createPlaylistBtn.isEnabled = isCreateAllowed

                createBtnIsAllowed(isCreateAllowed)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.ivPickerCover.setOnClickListener {
            lifecycleScope.launch {
                val readPermission = requester.request(Manifest.permission.READ_MEDIA_IMAGES)

                readPermission.collect { resultReading ->
                    when (resultReading) {
                        is PermissionResult.Granted -> {
                            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }

                        is PermissionResult.Denied.NeedsRationale -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.permission_needs_rationale,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is PermissionResult.Denied.DeniedPermanently -> {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.data = Uri.fromParts("package", context?.packageName, null)
                            context?.startActivity(intent)
                        }

                        PermissionResult.Cancelled -> {
                            return@collect
                        }
                    }
                }
            }
        }

        binding.ibBack.setOnClickListener {
            showDialogIfItNecessary()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showDialogIfItNecessary()
        }

        binding.createPlaylistBtn.setOnClickListener {
            viewModel.addNewPlaylistToDb(createNewPlaylist())
            (activity as? BottomNavBarShower)?.showNavbar()
            requireActivity().supportFragmentManager.popBackStack()
            Toast.makeText(
                requireContext().applicationContext,
                requireContext().getString(R.string.playlist_created, binding.etTitle.text),
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }


    private fun chooseImageAndSave(uri: Uri): String {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "album")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)


        return file.absolutePath
    }

    private fun loadImage(): String {

        val file = if (!viewModel.getImagePath().isNullOrEmpty()) {
            File(viewModel.getImagePath()!!)
        } else {
            File("android.resource://${requireContext().packageName}/drawable/placeholder")
        }
        Glide.with(binding.ivPickerCover)
            .load(file)
            .transform(
                CenterCrop(),RoundedCorners(dpToPx(8f, requireContext()))
            )
            .into(binding.ivPickerCover)

        return file.absolutePath
    }

    private fun createNewPlaylist(): Playlist {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val filePath = loadImage()
        return Playlist(
            playlistId = 0,
            title = title,
            description = description,
            filePath = filePath,
            trackIds = null,
            trackCount = null
        )
    }

    private fun setUpDialog() {

        confirmCloseCreatingDialog = MaterialAlertDialogBuilder(requireContext(),R.style.DialogTheme)
            .setTitle(R.string.confirm_ending_playlist_creating)
            .setMessage(R.string.confirm_ending_playlist_creating_message)
            .setNegativeButton(R.string.dialog_negative_btn) { dialog, which ->
                dialog.dismiss()

            }
            .setPositiveButton(R.string.dialog_positive_btn) { dialog, which ->
                navigateBack()
            }

    }

    private fun showDialogIfItNecessary() {

        val hasUnsavedData =
            isImageSelected ||
                    !binding.etTitle.text.isNullOrEmpty() ||
                    !binding.etDescription.text.isNullOrEmpty()

        if (hasUnsavedData) {
            confirmCloseCreatingDialog.show()

        } else {
            navigateBack()
        }
    }

    private fun createBtnIsAllowed(isAllowed: Boolean) {
        when (isAllowed) {
            true -> binding.createPlaylistBtn.setBackgroundColor(requireContext().getColor(R.color.YP_blue))
            false -> {
                binding.createPlaylistBtn.setBackgroundColor(requireContext().getColor(R.color.just_gray))
            }
        }
    }

    private fun navigateBack() {
        val previousScreen = arguments?.getString(PREVIOUS_SCREEN)
        when (previousScreen) {
            "AudioPlayerActivity" -> {
                requireActivity().finish()
            }

            else -> {
                (activity as? BottomNavBarShower)?.showNavbar()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}