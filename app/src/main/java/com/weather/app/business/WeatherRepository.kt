package com.weather.app.business

import androidx.compose.runtime.saveable.autoSaver
import com.weather.app.auth.NetworkService
import com.weather.app.models.CurrentWeatherModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val networkService: NetworkService
) : WeatherRepoImpl {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        appId: String
    ): Flow<CurrentWeatherModel> {
        return flow {
            emit(networkService.getCurrentWeather(lat,lon,appId))
        }
    }
}