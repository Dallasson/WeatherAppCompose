package com.weather.app.business

import com.weather.app.models.CurrentWeatherModel
import kotlinx.coroutines.flow.Flow

interface WeatherRepoImpl {

    suspend fun getCurrentWeather(lat : Double , lon : Double , appId : String) :
            Flow<CurrentWeatherModel>
}