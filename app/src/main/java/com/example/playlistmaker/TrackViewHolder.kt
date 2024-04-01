package com.example.playlistmaker


import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivPrint = itemView.findViewById<ImageView>(R.id.iv_print)
    private val tvTiming = itemView.findViewById<TextView>(R.id.tv_timing)
    private val tvTrack: TextView = itemView.findViewById(R.id.tv_track)
    private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun bind(item: Track,listenerItem: OnClickListenerItem) {
        val url = item.artworkUrl100
        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f, itemView.context )))
            .into(ivPrint)
        tvTrack.text = item.trackName
        tvAuthor.text = item.artistName
        tvTiming.text = dateFormat.format(item.trackTimeMillis)

        itemView.setOnClickListener{
            listenerItem.onItemClick(item)
        }

    }

fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}