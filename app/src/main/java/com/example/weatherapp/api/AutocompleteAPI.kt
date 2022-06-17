package com.example.weatherapp.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AutocompleteAPI {
    @GET("/v1/geocode/autocomplete?")
    suspend fun getLocation(@Query("text") location : String, @Query("format") format : String = "json",
                           @Query("apiKey") apikey : String = "68510f1a507c48d5ae211061ff9ff239"): Response<String>

    companion object {
        val BASE_URL = "https://api.geoapify.com"
        fun create() : AutocompleteAPI {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(AutocompleteAPI::class.java)
        }
    }
}