package com.weather.app.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.app.models.CurrentWeatherModel
import com.weather.app.models.week.WeekWeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepoImpl: WeatherRepoImpl
): ViewModel() {


    private val _currentWeather : MutableStateFlow<UiStates> = MutableStateFlow(UiStates.INITIAL)
    val currentWeather get() = _currentWeather.asStateFlow()


    private val _monthlyWeather : MutableStateFlow<UiStates> = MutableStateFlow(UiStates.INITIAL)
    val monthlyWeather get() = _monthlyWeather.asStateFlow()


    fun getCurrentWeather(lat : Double , lon : Double , unit : String , appId : String){
        viewModelScope.launch {
            try {
                _currentWeather.value = UiStates.LOADING
                weatherRepoImpl.getCurrentWeather(lat, lon, unit, appId).collectLatest {
                    _currentWeather.value = UiStates.SUCCESS(it)
                }
            } catch (ex : Exception){
                _currentWeather.value = UiStates.ERROR(ex.localizedMessage)
            }
        }
    }
    fun getMonthlyWeather(lat : Double , lon : Double , unit : String , appId : String){
        viewModelScope.launch {
            try {
                _monthlyWeather.value = UiStates.LOADING
                weatherRepoImpl.getMonthlyWeather(lat, lon, unit, appId).collectLatest {
                    _monthlyWeather.value = UiStates.WeekWeatherSuccess(it)
                }
            } catch (ex : Exception){
                _monthlyWeather.value = UiStates.ERROR(ex.localizedMessage)
            }
        }
    }

    sealed class UiStates {
        object LOADING : UiStates()
        data class SUCCESS(var currentWeatherModel: CurrentWeatherModel) : UiStates()
        data class WeekWeatherSuccess(var weekWeatherModel: WeekWeatherModel) : UiStates()
        data class ERROR(var error : String?) : UiStates()
        object INITIAL : UiStates()
    }
}