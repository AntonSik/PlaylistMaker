package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.LocalStorage
import com.example.playlistmaker.data.storage.db.TracksDatabase
import com.example.playlistmaker.utils.App.Companion.SHARED_PREFERENCES
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(context =  androidContext(), itunesService =  get())
    }
    single{
        LocalStorage(sharedPrefs =  get(), gson =  get())
    }
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = TracksDatabase::class.java,
            name = "database.db").fallbackToDestructiveMigration()
            .build()
    }
}