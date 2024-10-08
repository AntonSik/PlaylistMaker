package com.example.playlistmaker.ui.media.playlists

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistViewHolder(
    private val binding: ItemPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Playlist) {
        val filePath = item.filePath
        Glide.with(itemView)
            .load(filePath)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(), RoundedCorners(dpToPx(8f, itemView.context))
            )
            .into(binding.ivCoverItemPleylist)

        binding.tvTitle.text = item.title
        binding.tvCountTracks.text = changingTracksCountEnding(item.trackCount)

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun changingTracksCountEnding(tracksCount: Int?): String {
        val trackEnding: String
        val defaultEnding = "0 треков"
        if (tracksCount == null) {
            return defaultEnding
        } else {
            val firstNumber = tracksCount % 100
            val lastNumber = tracksCount % 10
            trackEnding = if (firstNumber in 10..20) {
                "треков"
            } else if (lastNumber in 2..5) {
                "трека"
            } else if (lastNumber == 1) {
                "трек"
            } else {
                "треков"
            }
            return "$tracksCount $trackEnding"
        }
    }
}
