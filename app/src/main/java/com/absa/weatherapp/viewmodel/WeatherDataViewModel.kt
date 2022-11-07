package com.absa.weatherapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.absa.weatherapp.R
import com.absa.weatherapp.api.FetchError
import com.absa.weatherapp.api.ResponseData
import com.absa.weatherapp.api.WeatherAPI
import com.absa.weatherapp.model.LocationData
import com.absa.weatherapp.model.Weather
import com.absa.weatherapp.model.WeatherData
import com.absa.weatherapp.utill.ADDRESS_CALL
import com.absa.weatherapp.utill.WEATHER_CALL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class WeatherDataViewModel(application: Application) : AndroidViewModel(application) {

    private val _failedMessage = MutableLiveData<String>(null)
    val failedMessage: LiveData<String> get() = _failedMessage

    private val _weatherData = MutableLiveData<WeatherData<Weather>>(null)
    val weatherData: LiveData<WeatherData<Weather>> get() = _weatherData

    private val _locationData = MutableLiveData<LocationData>(null)
    val locationData: LiveData<LocationData> get() = _locationData

    private val _weatherIcon = MutableLiveData<Bitmap>(null)
    val weatherIcon: LiveData<Bitmap> get() = _weatherIcon

    private val _showWeatherProgress = MutableLiveData(false)
    val showWeatherProgress: LiveData<Boolean> get() = _showWeatherProgress

    private fun getApiKey(): String {
        return getApplication<Application>().resources.getString(R.string.api_key)
    }

    private fun getUnit(): String {
        return getApplication<Application>().resources.getString(R.string.unit)
    }

    private fun getString(resourceString: Int): String {
        return getApplication<Application>().resources.getString(resourceString)
    }

    private fun getWeatherData(
        latitude: Float,
        longitude: Float,
        apiKey: String
    ) {
        _showWeatherProgress.postValue(true)
        viewModelScope.launch {
            WeatherAPI.create().getWeatherData(
                latitude, longitude, apiKey, getUnit(),
                this@WeatherDataViewModel::success,
                this@WeatherDataViewModel::failure
            )
        }
    }

    fun getLocationFromAddress(searchString: String) {
        _showWeatherProgress.postValue(true)
        viewModelScope.launch {
            WeatherAPI.create().getLocationFromAddress(
                searchString, getApiKey(),
                this@WeatherDataViewModel::success,
                this@WeatherDataViewModel::failure
            )
        }
    }

    fun getWeatherIcon(icon: String?) {
        icon?.run {
            viewModelScope.launch {
                val d = viewModelScope.async(Dispatchers.IO) {
                    BitmapFactory.decodeStream(
                        URL("https://openweathermap.org/img/wn/$icon@2x.png").openConnection()
                            .getInputStream()
                    )
                }
                _weatherIcon.postValue(d.await())
            }
        }
    }

    private fun success(responseData: ResponseData, call: Int) {
        when (call) {
            ADDRESS_CALL -> {
                val locationDataArray = responseData as ArrayList<LocationData>
                if (locationDataArray.size > 0) {
                    getWeatherData(locationDataArray[0].latitude!!, locationDataArray[0].longitude!!, getApiKey())
                    _locationData.postValue(locationDataArray[0])
                } else failure(getString(R.string.valid_location_error))
            }
            WEATHER_CALL -> {
                _weatherData.postValue(responseData as WeatherData<Weather>)
                _showWeatherProgress.postValue(false)
            }
        }
    }

    private fun failure(error: FetchError) {
        _showWeatherProgress.postValue(false)
        _failedMessage.postValue(error as String)
    }
}