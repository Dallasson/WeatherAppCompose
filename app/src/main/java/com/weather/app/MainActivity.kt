package com.weather.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weather.app.business.WeatherViewModel
import com.weather.app.extras.Utils
import com.weather.app.routing.AppRouting
import com.weather.app.ui.theme.WeatherAppTheme
import com.weather.app.views.CurrentWeatherScreen
import com.weather.app.views.WeekWeatherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val weatherViewModel : WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme(
                darkTheme = true
            ) {
                // A surface container using the 'background' color from the theme
                var navHostController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   CreateNavHost(navHostController, weatherViewModel)
                }
            }
        }
    }
}


@Composable
fun CreateNavHost(navHostController: NavHostController,weatherViewModel: WeatherViewModel){
    NavHost(navController = navHostController, startDestination = AppRouting.CurrentWeatherScreen.route){
        composable(AppRouting.CurrentWeatherScreen.route){
            weatherViewModel.getCurrentWeather(35.4011,8.1173,"metric", Utils.API_KEY)
            CurrentWeatherScreen(weatherViewModel = weatherViewModel, navHostController = navHostController)
        }
        composable(AppRouting.WeekWeatherScreen.route){
            WeekWeatherScreen()
        }
    }
}
