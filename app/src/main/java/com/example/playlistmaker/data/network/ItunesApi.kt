package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.SearchTrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    suspend fun search(
        @Query("term") text: String
    ): SearchTrackResponse
}