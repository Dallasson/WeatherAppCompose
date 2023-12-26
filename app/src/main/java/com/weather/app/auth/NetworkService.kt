package com.weather.app.auth

import com.weather.app.models.CurrentWeatherModel
import com.weather.app.models.week.WeekWeatherModel
import com.weather.app.routing.AppRouting
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    @GET("weather?")
    suspend fun getCurrentWeather(
        @Query("lat") lat : Double?,
        @Query("lon") lon : Double?,
        @Query("units") unit : String? ,
        @Query("appid") apiKey : String?
    ) : CurrentWeatherModel


    @GET("forecast?")
    suspend fun getMonthForecast(
        @Query("lat") lat : Double?,
        @Query("lon") lon : Double?,
        @Query("units") unit: String?,
        @Query("appid") apiKey : String?
    ) : WeekWeatherModel
}