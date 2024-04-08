package com.example.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryPrefs(context: Context) {
    companion object {
        const val SHARED_PREFERENCES_HISTORY = "Shared pref's key"
        private const val KEY_FOR_TRACK_LIST = "New List's key"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val sharedPrefs =
        context.getSharedPreferences(SHARED_PREFERENCES_HISTORY, Context.MODE_PRIVATE)

    private fun saveList(historyTrackList: ArrayList<Track>) {    // Метод сохранения листа в sharedPreferences

        val trackListJson = Gson().toJson(historyTrackList)

        sharedPrefs.edit()
            .putString(KEY_FOR_TRACK_LIST, trackListJson)
            .apply()
    }

    fun getHistoryList(): ArrayList<Track> {     // Метод получения листа из sharedPreferences

        val typeList = object : TypeToken<ArrayList<Track>>() {}.type
        val savedTrackListJson = sharedPrefs.getString(KEY_FOR_TRACK_LIST, null)
        if (savedTrackListJson != null) {
            return Gson().fromJson(savedTrackListJson, typeList)

        }
        return arrayListOf()
    }

    fun addTrack(track: Track) {     //добавление трека в лист
        val historyList = getHistoryList()

        if (historyList.size > MAX_HISTORY_SIZE) {
            historyList.removeAt(historyList.size - 1)
        }
        if (historyList.contains(track)) historyList.remove(track)

        historyList.add(0, track)
        saveList(historyList)
    }

    fun clearHistory() {
        val historyList = getHistoryList()
        historyList.clear()
        saveList(historyList)
    }

}