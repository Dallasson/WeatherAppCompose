package com.weather.app.auth

import com.weather.app.models.CurrentWeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("weather?")
    suspend fun getCurrentWeather(
        @Query("lon") lat : Double?,
        @Query("lon") lon : Double?,
        @Query("appid") apiKey : String?
    ) : CurrentWeatherModel
}