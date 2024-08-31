package com.example.playlistmaker.domain.models


import android.os.Parcel
import android.os.Parcelable


data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val primaryGenreName: String,
    val releaseDate: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(trackId)
        dest.writeString(trackName)
        dest.writeString(artistName)
        dest.writeLong(trackTimeMillis)
        dest.writeString(artworkUrl100)
        dest.writeString(collectionName)
        dest.writeString(primaryGenreName)
        dest.writeString(releaseDate)
        dest.writeString(country)
        dest.writeString(previewUrl)
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(source: Parcel): Track {
            return Track(
                trackId = source.readInt(),
                trackName = source.readString() ?: "",
                artistName = source.readString() ?: "",
                trackTimeMillis = source.readLong(),
                artworkUrl100 = source.readString() ?: "",
                collectionName = source.readString() ?: "",
                primaryGenreName = source.readString() ?: "",
                releaseDate = source.readString() ?: "",
                country = source.readString() ?: "",
                previewUrl = source.readString() ?: "",
                isFavorite = source.readBoolean()
            )
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}
