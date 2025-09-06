package com.example.myapptranclatesky.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class WordResponse(
    val id: Long,
    val text: String,
    val meanings: List<Meaning>
) {
    data class Meaning(
        val id: Long,
        val translation: Translation?
    ) {
        data class Translation(val text: String?)
    }
}

interface SkyengService {
    @GET("/api/public/v1/words/search")
    suspend fun search(@Query("search") query: String): List<WordResponse>
}
