package com.weather.app.business

import com.weather.app.models.CurrentWeatherModel
import com.weather.app.models.week.WeekWeatherModel
import kotlinx.coroutines.flow.Flow

interface WeatherRepoImpl {

    suspend fun getCurrentWeather(lat : Double , lon : Double , unit : String ,  appId : String) :
            Flow<CurrentWeatherModel>

    suspend fun getMonthlyWeather(lat: Double,lon: Double,unit: String,appId: String) :
            Flow<WeekWeatherModel>
}