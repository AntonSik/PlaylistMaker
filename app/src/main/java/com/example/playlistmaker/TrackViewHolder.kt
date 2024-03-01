package com.example.playlistmaker


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivPrint = itemView.findViewById<ImageView>(R.id.iv_print)
    private val tvTiming = itemView.findViewById<TextView>(R.id.tv_timing)
    private val tvTrack: TextView = itemView.findViewById(R.id.tv_track)
    private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)

    fun bind(item: Track) {
        val url = item.artworkUrl100
        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.not_found_icon)
            .fitCenter()
            .transform(RoundedCorners(10))
            .into(ivPrint)
        tvTrack.text = item.trackName
        tvAuthor.text = item.artistName
        tvTiming.text = item.trackTime

    }
}