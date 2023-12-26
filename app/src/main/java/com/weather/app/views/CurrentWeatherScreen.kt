package com.weather.app.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.weather.app.R
import com.weather.app.business.WeatherViewModel
import com.weather.app.extras.Utils
import com.weather.app.extras.Utils.LOCATION_PERMISSIONS_REQUEST_CODE
import com.weather.app.extras.Utils.REQUEST_CHECK_SETTINGS
import com.weather.app.models.CurrentWeatherModel
import com.weather.app.models.location.LatLngModel
import com.weather.app.routing.AppRouting
import okhttp3.internal.wait
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherScreen(
    weatherViewModel: WeatherViewModel,
    navHostController: NavHostController) {

    val locationState = remember { mutableStateOf(LatLngModel()) }
    val uiStates = weatherViewModel.currentWeather.collectAsState()
    val fieldState = remember { mutableStateOf("") }
    val context = LocalContext.current

    if(!grantLocationPermission(context)){
        requestLocation(context)
    } else {
        getUserLocation(context,locationState)
    }


    Column {

        Row(modifier = Modifier.padding(start = 20.dp , end = 20.dp , top = 15.dp)) {

            Icon(
                painter = painterResource(id = R.drawable.list),
                contentDescription = "",
                Modifier.size(20.dp))

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { navHostController.navigate(AppRouting.WeekWeatherScreen.route) }) {
                Icon(painter = painterResource(id = R.drawable.seven),
                    contentDescription = "",
                    Modifier.size(20.dp),
                    tint = Color.Unspecified)
            }

        }

        Text(
            text = "Today's Weather",
            style = TextStyle(fontFamily = FontFamily(Font(R.font.ubuntub))),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 20.dp , end = 20.dp , top = 20.dp))


        TextField(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(color = Color.DarkGray),
            value = fieldState.value,
            textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.ubuntum)), fontSize = 8.sp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.DarkGray,
            ),
            trailingIcon = { IconButton(onClick = {
                if(fieldState.value.isNotEmpty()){
                    Toast.makeText(context,"Toast Shown",Toast.LENGTH_LONG).show()
                    getLatLongFromLocationName(context,fieldState.value,weatherViewModel)
                }
            }) { Icon(Icons.Filled.Search, tint = Color.White, contentDescription = "") }},
            onValueChange = { fieldState.value = it })

        when(val currentState = uiStates.value){
            is WeatherViewModel.UiStates.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is WeatherViewModel.UiStates.SUCCESS -> {
                PopulateUi(currentWeatherModel = currentState.currentWeatherModel)
            }
            is WeatherViewModel.UiStates.ERROR -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = currentState.error.toString())
                }
            }
            else -> {}
        }

    }
}

fun getLatLongFromLocationName(context: Context, locationName: String?, weatherViewModel: WeatherViewModel) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addressList: MutableList<Address>? = geocoder.getFromLocationName(locationName!!, 1)
        if (addressList?.size!! > 0) {
            val address: Address = addressList[0]
            val latitude: Double = address.latitude
            val longitude: Double = address.longitude
            weatherViewModel.getCurrentWeather(latitude,longitude,"metric", Utils.API_KEY)
        } else {
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d("Weather Tag","Error "  + e.localizedMessage)
    }
}

@Composable
fun PopulateUi(currentWeatherModel: CurrentWeatherModel){

    Row(modifier = Modifier
        .padding(start = 20.dp, end = 20.dp, top = 30.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(color = Color.DarkGray)
        .height(150.dp)) {

        AsyncImage(
            model = "https://openweathermap.org/img/w/" + currentWeatherModel.weather[0].icon + ".png",
            contentDescription = "",
            modifier = Modifier
                .width(120.dp)
                .height(120.dp))

        Column(verticalArrangement = Arrangement.SpaceEvenly) {

            Text(
                text = currentWeatherModel.weather[0].description,
                fontFamily = FontFamily(Font(R.font.ubuntum)),
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp))

            Spacer(modifier = Modifier.padding(top = 15.dp))

            Text(
                text = formatValue(currentWeatherModel.main.temp.toString()) + "°C",
                fontFamily = FontFamily(Font(R.font.ubuntub)),
                color = Color.White,
                fontSize = 35.sp,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.padding(top = 10.dp))


            Text(
                text = convertTimestampToFormattedDate(currentWeatherModel.dt),
                fontFamily = FontFamily(Font(R.font.ubuntum)),
                color = Color.White,
                fontSize = 12.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
        }
    }

    // WORKING HERE

    Card(modifier = Modifier
        .padding(20.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(color = Color.DarkGray)) {

        Column(Modifier.fillMaxWidth()) {

            Row(Modifier.padding(top = 20.dp)) {

                Column(modifier = Modifier.height(100.dp).width(100.dp).padding(start = 20.dp, end = 20.dp)) {

                    Icon(
                        painter = painterResource(id = R.drawable.feel),
                        contentDescription = "",
                        modifier = Modifier
                            .size(25.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        tint = Color.White)

                    Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Divider(color = Color.White,modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Text(text = formatValue(currentWeatherModel.main.feels_like.toString() + "°C"),
                        fontFamily = FontFamily(Font(R.font.ubuntum)),
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                }
                Column(modifier = Modifier.height(100.dp).width(100.dp).padding(start = 20.dp, end = 20.dp)) { Icon(
                        painter = painterResource(id = R.drawable.min),
                        contentDescription = "",
                        modifier = Modifier
                            .size(25.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        tint = Color.Unspecified)

                    Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Divider(color = Color.White,modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Text(text = formatValue(currentWeatherModel.main.temp_min.toString() + "°C"),
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        fontFamily = FontFamily(Font(R.font.ubuntum))
                    )
                }
                Column(modifier = Modifier.height(100.dp).width(100.dp).padding(start = 20.dp, end = 20.dp)) {

                    Icon(
                        painter = painterResource(id = R.drawable.max),
                        contentDescription = "",
                        modifier = Modifier
                            .size(25.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        tint = Color.Unspecified)

                    Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Divider(color = Color.White,modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Text(text = formatValue(currentWeatherModel.main.temp_max.toString()) + "°C",
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        fontFamily = FontFamily(Font(R.font.ubuntum))
                    )
                }

            }

            Row() {

                Column(modifier = Modifier.height(100.dp).width(100.dp).padding(start = 20.dp, end = 20.dp)) {

                    Icon(
                        painter = painterResource(id = R.drawable.pressure),
                        contentDescription = "",
                        modifier = Modifier
                            .size(25.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        tint = Color.White)

                    Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Divider(color = Color.White,modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Text(text = formatValue(currentWeatherModel.main.pressure.toString() + "°C"),
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        fontFamily = FontFamily(Font(R.font.ubuntum))
                    )
                }
                Column(modifier = Modifier.height(100.dp).width(100.dp).padding(start = 20.dp, end = 20.dp)) { Icon(

                    painter = painterResource(id = R.drawable.humidity),
                    contentDescription = "",
                    modifier = Modifier
                        .size(25.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    tint = Color.Unspecified)

                    Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Divider(color = Color.White,modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Text(text = formatValue(currentWeatherModel.main.humidity.toString()) + "°C",
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        fontFamily = FontFamily(Font(R.font.ubuntum))
                    )
                }
                Column(modifier = Modifier.height(100.dp).width(100.dp).padding(start = 20.dp, end = 20.dp)) {

                    Icon(
                        painter = painterResource(id = R.drawable.sea),
                        contentDescription = "",
                        modifier = Modifier
                            .size(25.dp)
                            .align(alignment = Alignment.CenterHorizontally),
                        tint = Color.Unspecified)

                    Spacer(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Divider(color = Color.White,modifier = Modifier.padding(top = 5.dp, bottom = 5.dp))

                    Text(text = formatValue(currentWeatherModel.main.sea_level.toString()) + "°C",
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                        fontFamily = FontFamily(Font(R.font.ubuntum))
                    )
                }

            }
        }
    }

}

fun convertTimestampToFormattedDate(timestamp: Int): String {
    // Convert timestamp to Date
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp.toLong()

    // Define the desired date format
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US)

    // Format the date and return the result
    return dateFormat.format(calendar.time)
}
fun grantLocationPermission(context : Context) : Boolean {
    return (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
}
fun requestLocation(context: Context){
    ActivityCompat.requestPermissions((context as Activity), arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ),LOCATION_PERMISSIONS_REQUEST_CODE)
}

lateinit var locationCallback: LocationCallback
lateinit var locationRequest: LocationRequest
lateinit var fusedLocationProviderClient: FusedLocationProviderClient
@SuppressLint("MissingPermission")
fun getUserLocation(context: Context,locationState : MutableState<LatLngModel>){

    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    locationRequest = LocationRequest.create()
    locationRequest.interval = 5000L
    locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    locationCallback = object : LocationCallback(){

        override fun onLocationAvailability(locationAvailability : LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
        }

        override fun onLocationResult(locationResult : LocationResult) {
            super.onLocationResult(locationResult)
            if(locationResult.lastLocation != null){
                val location = locationResult.lastLocation
                locationState.value = LatLngModel(location?.latitude,location?.longitude)
            }
        }
    }

    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    val locationSettings = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .build()

    val task = LocationServices.getSettingsClient(context).checkLocationSettings(locationSettings)
    task.addOnSuccessListener {  }
    task.addOnFailureListener { e ->
        if(e is ApiException){
            val statusCode = e.statusCode
            if(statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                if(e is ResolvableApiException) {
                    e.startResolutionForResult((context as Activity), REQUEST_CHECK_SETTINGS)
                }
            }
        }
    }
}

private fun formatValue(value : String) : String {
    if(value.contains(".")){
        return value
    }
    return value.substringBefore(".")
}

