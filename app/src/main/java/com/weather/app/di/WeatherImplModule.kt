package com.weather.app.di

import com.weather.app.business.WeatherRepoImpl
import com.weather.app.business.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherImplModule {


    @Binds
    abstract fun getWeatherImpl(weatherRepository: WeatherRepository) : WeatherRepoImpl
}