package com.absa.weatherapp.api


import com.absa.weatherapp.model.LocationData
import com.absa.weatherapp.model.Weather
import com.absa.weatherapp.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherWebAPI {

    @GET("data/2.5/weather/")
    fun getWeatherData(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherData<Weather>>

    @GET("geo/1.0/direct")
    fun getLocationFromAddress(
        @Query("q") searchString: String,
        @Query("appid") apiKey: String
    ): Call<ArrayList<LocationData>>
}


