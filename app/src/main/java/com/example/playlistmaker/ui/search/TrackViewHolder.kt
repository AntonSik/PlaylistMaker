package com.example.playlistmaker.ui.search


import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TrackViewHolder(
    private val binding: ItemTrackBinding,

) : RecyclerView.ViewHolder(binding.root) {
    private val ivPrint = itemView.findViewById<ImageView>(R.id.iv_print)
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun bind(item: Track) {
        val url = item.artworkUrl100
        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(ivPrint)
        binding.tvTrack.text = item.trackName
        binding.tvAuthor.text = item.artistName
        binding.tvTiming.text = dateFormat.format(item.trackTimeMillis)

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}