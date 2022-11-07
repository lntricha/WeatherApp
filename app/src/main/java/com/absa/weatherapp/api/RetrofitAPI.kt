package com.absa.weatherapp.api

import android.util.Log
import com.absa.weatherapp.ADDRESS_CALL
import com.absa.weatherapp.BuildConfig
import com.absa.weatherapp.WEATHER_CALL
import com.absa.weatherapp.model.LocationData
import com.absa.weatherapp.model.Weather
import com.absa.weatherapp.model.WeatherData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitAPI(baseUrl: String) : API {

    private var retrofitAPI: WebAPI
    private val callTimeout = 0L
    private val connectTimeout = 5L
    private val readTimeout = 5L
    private val writeTimeout = 5L
    private val successCode = 200

    init {
        val httpClient = OkHttpClient.Builder()
            .callTimeout(callTimeout, TimeUnit.SECONDS)
            .connectTimeout(connectTimeout, TimeUnit.MINUTES)
            .readTimeout(readTimeout, TimeUnit.MINUTES)
            .writeTimeout(writeTimeout, TimeUnit.MINUTES)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl) // host url
            .client(httpClient.build())
            .build()

        retrofitAPI = retrofit.create(WebAPI::class.java)
    }

    override suspend fun getWeatherData(
        lat: Float,
        lon: Float,
        apiKey: String,
        units: String,
        success: (DataList, Int) -> Unit,
        failure: (FetchError) -> Unit
    ) {
        retrofitAPI.getWeatherData(lat, lon, apiKey, units).enqueue(object : Callback<WeatherData<Weather>> {
            override fun onResponse(
                call: Call<WeatherData<Weather>>,
                response: Response<WeatherData<Weather>>
            ) {
                if (response.isSuccessful && response.code() == successCode) {
                    if (BuildConfig.DEBUG)
                        Log.d("List", response.body().toString())
                    val dataList = response.body()
                    dataList?.let {
                        success(dataList, WEATHER_CALL)
                    }
                } else {
                    if (BuildConfig.DEBUG)
                        Log.d("onFailure", response.message())
                    failure(response.message())
                }
            }

            override fun onFailure(call: Call<WeatherData<Weather>>, t: Throwable) {
                if (BuildConfig.DEBUG)
                    Log.d("onFailure", t.message.toString())
                failure(t.message.toString())
            }
        })
    }

    override suspend fun getLocationFromAddress(
        searchString: String,
        apiKey: String,
        success: (DataList, Int) -> Unit,
        failure: (FetchError) -> Unit
    ) {
        retrofitAPI.getLocationFromAddress(searchString, apiKey).enqueue(object : Callback<ArrayList<LocationData>> {
            override fun onResponse(
                call: Call<ArrayList<LocationData>>,
                response: Response<ArrayList<LocationData>>
            ) {
                if (response.isSuccessful && response.code() == successCode) {
                    if (BuildConfig.DEBUG)
                        Log.d("Location", response.body().toString())
                    val dataList = response.body()
                    dataList?.let {
                        success(dataList, ADDRESS_CALL)
                    }
                } else {
                    if (BuildConfig.DEBUG)
                        Log.d("onFailure", response.message())
                    failure(response.message())
                }
            }

            override fun onFailure(call: Call<ArrayList<LocationData>>, t: Throwable) {
                if (BuildConfig.DEBUG)
                    Log.d("onFailure", t.message.toString())
                failure(t.message.toString())
            }
        })
    }
}

