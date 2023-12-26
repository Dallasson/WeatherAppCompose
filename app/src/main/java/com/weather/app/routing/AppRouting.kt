package com.weather.app.routing


sealed class AppRouting(var route : String) {
   object CurrentWeatherScreen : AppRouting("current")
   object WeekWeatherScreen : AppRouting("week")
}