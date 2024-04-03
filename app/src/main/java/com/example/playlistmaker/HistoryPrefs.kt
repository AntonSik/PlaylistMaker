package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class HistoryPrefs(context: Context) {
    companion object {
        private const val SHARED_PREFERENCES_HISTORY = "Shared pref's key"
        private const val KEY_FOR_NEW_TRACK = "New track's key"
    }

    val sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES_HISTORY, Context.MODE_PRIVATE)

    fun init(historyList: ArrayList<Track>) {   //Получаем на вход лист для хранения истории поиска
        val jsonString = Gson().toJson(historyList) // конвертируем его в строку
        sharedPrefs.edit()
            .putString(KEY_FOR_NEW_TRACK, jsonString)  // кладем строку в sharedPrefs
            .apply()
    }
    fun add(track:Track){
        val historyList = getValue()  //записываем в переменную лист
        historyList.add(track)   //Добавляем в него трек
        init(historyList)
    }
    fun getValue():ArrayList<Track> {
        val historyList = ArrayList<Track>()   //переменная для хранения истории
        val jsonString = sharedPrefs.getString(KEY_FOR_NEW_TRACK,null)     //получаем строку из sharedPrefs
        val UpdatedHistoryList = Gson().fromJson(jsonString,historyList ::class.java) //конвертируем строку в Лист
        return UpdatedHistoryList
    }
}