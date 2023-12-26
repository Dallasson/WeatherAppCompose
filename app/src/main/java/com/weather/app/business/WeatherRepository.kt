package com.weather.app.business

import com.weather.app.auth.NetworkService
import com.weather.app.models.CurrentWeatherModel
import com.weather.app.models.week.WeekWeatherModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val networkService: NetworkService
) : WeatherRepoImpl {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        unit : String ,
        appId: String
    ): Flow<CurrentWeatherModel> {
        return flow {
            emit(networkService.getCurrentWeather(lat,lon,unit,appId))
        }
    }

    override suspend fun getMonthlyWeather(
        lat: Double,
        lon: Double,
        unit: String,
        appId: String
    ): Flow<WeekWeatherModel> {
        return flow {
            emit(networkService.getMonthForecast(lat, lon,unit,appId))
        }
    }
}