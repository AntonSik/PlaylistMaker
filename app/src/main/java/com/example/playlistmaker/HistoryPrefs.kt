package com.example.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryPrefs(context: Context) {
    companion object {
        private const val SHARED_PREFERENCES_HISTORY = "Shared pref's key"
        private const val KEY_FOR_TRACK_LIST = "New List's key"
    }

    val sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_HISTORY, Context.MODE_PRIVATE)

    fun save(historyTrackList: ArrayList<Track>) {

    val trackListJson = Gson().toJson(historyTrackList)

    sharedPrefs.edit()
        .putString(KEY_FOR_TRACK_LIST, trackListJson)
        .apply()
}
    fun get(): ArrayList<Track>{     // Метод получения

        val typeList =  object : TypeToken<ArrayList<Track>>() {}.type
        val savedTrackListJson = sharedPrefs.getString(KEY_FOR_TRACK_LIST, null)
        if(savedTrackListJson != null) {
            val savedTrackList: ArrayList<Track> = Gson().fromJson(savedTrackListJson, typeList)
            return savedTrackList
        }
        return arrayListOf()
    }
    fun add(track: Track){
        val historyList = get()

        if(historyList.size > 10){
            historyList.removeAt(historyList.size - 1)
        }
        if(historyList.contains(track)) historyList.remove(track)

        historyList.add(0,track)
        save(historyList)
    }

}