package com.absa.weatherapp

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.absa.weatherapp.api.API
import com.absa.weatherapp.api.DataList
import com.absa.weatherapp.api.FetchError
import com.absa.weatherapp.model.LocationData
import com.absa.weatherapp.model.Weather
import com.absa.weatherapp.model.WeatherData
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

    private val _icon = MutableLiveData<Bitmap>(null)
    val icon: LiveData<Bitmap> get() = _icon

    private val _showProgress = MutableLiveData(false)
    val showProgress: LiveData<Boolean> get() = _showProgress

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
        lat: Float,
        lon: Float,
        apiKey: String
    ) {
        _showProgress.postValue(true)
        viewModelScope.launch {
            API.create().getWeatherData(
                lat, lon, apiKey, getUnit(),
                this@WeatherDataViewModel::success,
                this@WeatherDataViewModel::failure
            )
        }
    }

    fun getLocationFromAddress(searchString: String, apiKey: String) {
        _showProgress.postValue(true)
        viewModelScope.launch {
            API.create().getLocationFromAddress(
                searchString, apiKey,
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
                _icon.postValue(d.await())
            }
        }
    }

    private fun success(dataList: DataList, call: Int) {
        if (BuildConfig.DEBUG)
            Log.d("List My", dataList.toString())
        when (call) {
            ADDRESS_CALL -> {
                val locationDataArray = dataList as ArrayList<LocationData>
                if (locationDataArray.size > 0) {
                    getWeatherData(locationDataArray[0].lat!!, locationDataArray[0].lon!!, getApiKey())
                    _locationData.postValue(locationDataArray[0])
                } else failure(getString(R.string.valid_location_error))
            }
            WEATHER_CALL -> {
                _weatherData.postValue(dataList as WeatherData<Weather>)
                _showProgress.postValue(false)
            }
        }
    }

    private fun failure(error: FetchError) {
        _showProgress.postValue(false)
        _failedMessage.postValue(error as String)
        if (BuildConfig.DEBUG)
            Log.d("List", error)
    }
}