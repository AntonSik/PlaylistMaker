package com.example.playlistmaker.domain.models

import android.os.Parcel
import android.os.Parcelable


data class Playlist(
    val playlistId: Int = 0,
    val title: String,
    val description: String?,
    val filePath: String?,
    val trackIds: String?,
    val trackCount: Int?
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(playlistId)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(filePath)
        parcel.writeString(trackIds)
        parcel.writeValue(trackCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(source: Parcel): Playlist {
            return Playlist(
                playlistId = source.readInt(),
                title = source.readString()!!,
                description = source.readString(),
                filePath = source.readString(),
                trackIds = source.readString(),
                trackCount = source.readInt()
            )
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }
    }
}