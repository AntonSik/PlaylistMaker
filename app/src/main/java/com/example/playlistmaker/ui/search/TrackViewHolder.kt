package com.example.playlistmaker.ui.search



import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

//class TrackViewHolder(itemView: View, private val listenerItem: TrackAdapter.OnClickListenerItem) : RecyclerView.ViewHolder(itemView) {
class TrackViewHolder(private val binding: ItemTrackBinding, private val listenerItem: TrackAdapter.OnClickListenerItem) : RecyclerView.ViewHolder(binding.root) {
    private val ivPrint = itemView.findViewById<ImageView>(R.id.iv_print)
//    private val tvTiming = itemView.findViewById<TextView>(R.id.tv_timing)
//    private val tvTrack: TextView = itemView.findViewById(R.id.tv_track)
//    private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    fun bind(item: Track) {
        val url = item.artworkUrl100
        Glide.with(itemView)
            .load(url)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(ivPrint)
//        tvTrack.text = item.trackName
//        tvAuthor.text = item.artistName
//        tvTiming.text = dateFormat.format(item.trackTimeMillis)

//        itemView.findViewById<TextView>(R.id.tv_track).text = item.trackName
//        itemView.findViewById<TextView>(R.id.tv_author).text = item.artistName
//        itemView.findViewById<TextView>(R.id.tv_timing).text = dateFormat.format(item.trackTimeMillis)
          binding.tvTrack.text = item.trackName
          binding.tvAuthor.text = item.artistName
          binding.tvTiming.text = dateFormat.format(item.trackTimeMillis)

        itemView.setOnClickListener {
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