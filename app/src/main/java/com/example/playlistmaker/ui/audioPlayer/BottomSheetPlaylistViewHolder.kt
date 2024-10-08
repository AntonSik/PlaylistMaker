package com.example.playlistmaker.ui.audioPlayer

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.domain.models.Playlist

class BottomSheetPlaylistViewHolder(
    private val binding: ItemPlaylistBottomSheetBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val ivCoverIcon = itemView.findViewById<ImageView>(R.id.iv_cover_icon)

    fun bind(item: Playlist) {
        val url = item.filePath
        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(ivCoverIcon)

        binding.tvPlaylistTitle.text = item.title
        binding.tvPlaylistCountTracks.text = changingTracksCountEnding(item.trackCount)
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
