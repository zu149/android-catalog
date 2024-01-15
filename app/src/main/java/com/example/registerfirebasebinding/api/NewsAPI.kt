package com.example.registerfirebasebinding.api

import com.example.registerfirebasebinding.constants.API_KEY
import com.example.registerfirebasebinding.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("/v2/top-headlines") //популрнный новости
    suspend fun getBreakingNews(
        @Query("apiKey")
        apiKey: String = API_KEY,

        @Query("country")
        countryCode: String = "us",

        @Query("page")
        pageNumber: Int = 1,
    ): Response<NewsResponse>

    @GET("/v2/everything")//все новости
    suspend fun searchNews(
        @Query("apiKey")
        apiKey: String = API_KEY,

        @Query("q")
        searchQuery: String,

        @Query("page")
        pageNumber: Int = 1,
    ): Response<NewsResponse>
}