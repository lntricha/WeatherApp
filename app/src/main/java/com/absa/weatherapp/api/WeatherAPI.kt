package com.absa.weatherapp.api

interface WeatherAPI {

    suspend fun getWeatherData(
        lat: Float,
        lon: Float,
        apiKey: String,
        units: String,
        success: (DataList, Int) -> Unit,
        failure: (FetchError) -> Unit
    )

    suspend fun getLocationFromAddress(
        searchString: String,
        apiKey: String,
        success: (DataList, Int) -> Unit,
        failure: (FetchError) -> Unit
    )

    companion object {
        private const val BASEURL = "https://api.openweathermap.org/"
        fun create(): WeatherAPI {
            return RetrofitAPI(baseUrl = BASEURL)
        }
    }
}

typealias DataList = Any
typealias FetchError = Any

