package com.weather.app.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.app.models.CurrentWeatherModel
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

    fun getCurrentWeather(lat : Double , lon : Double , appId : String){
        viewModelScope.launch {
            try {
                _currentWeather.value = UiStates.LOADING
                weatherRepoImpl.getCurrentWeather(lat, lon, appId).collectLatest {
                    _currentWeather.value = UiStates.SUCCESS(it)
                }
            } catch (ex : Exception){
                _currentWeather.value = UiStates.ERROR(ex.localizedMessage)
            }
        }
    }


    sealed class UiStates {
        object LOADING : UiStates()
        data class SUCCESS(var currentWeatherModel: CurrentWeatherModel) : UiStates()
        data class ERROR(var error : String?) : UiStates()
        object INITIAL : UiStates()
    }
}