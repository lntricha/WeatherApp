package com.absa.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.absa.weatherapp.model.LocationData
import com.absa.weatherapp.model.Weather
import com.absa.weatherapp.model.WeatherData
import kotlinx.android.synthetic.main.activity_weather_dashboard.*
import kotlin.math.roundToInt

class WeatherDashboardActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val peopleListViewModel: WeatherDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_dashboard)

        peopleListViewModel.showProgress.observe(this) {
            showProgress(it)
        }

        peopleListViewModel.failedMessage.observe(this) {
            it?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        peopleListViewModel.weatherData.observe(this) {
            it?.let { showData(it) }
        }

        peopleListViewModel.locationData.observe(this) {
            it?.let { showLocationData(it) }
        }

        peopleListViewModel.icon.observe(this) {
            it?.let { imgWeather.setImageBitmap(it) }
        }
    }

    private fun showProgress(isShow: Boolean) {
        if (isShow) {
            progressBarFetchData.visibility = View.VISIBLE
        } else
            progressBarFetchData.visibility = View.GONE
    }

    private fun showLocationData(locationData: LocationData) {
        locationData.run {
            groupView.visibility = View.VISIBLE
            if (BuildConfig.DEBUG)
                Log.d("newList", this.toString())
            val location: StringBuilder = StringBuilder(name ?: "")
            state?.let { location.append(", $it") }
            country?.let { location.append(", $it") }
            tvCityCountry.text = location
        }
    }

    private fun showData(weatherData: WeatherData<Weather>) {
        weatherData.run {
            groupView.visibility = View.VISIBLE
            if (BuildConfig.DEBUG)
                Log.d("newList", this.toString())
            setData(this)

        }
    }

    private fun setData(weatherData: WeatherData<Weather>) {

        weatherData.temp?.run {
            tvTempValue.text = temp?.roundToInt().toString()
            tvTempFeels.text =
                String.format(getString(R.string.temp_feel) + " " + feels_like?.roundToInt().toString() + getString(R.string.temp_unit))
            tvHumidity.text = String.format(getString(R.string.humidity) + " " + humidity.toString()) + getString(R.string.percent)
            txtMinMaxTemp.text = String.format(temp_min?.roundToInt().toString() + " ~ " + temp_max?.roundToInt().toString() + getString(R.string.temp_unit))
        }
        weatherData.wind?.run {
            tvWind.text = String.format(getString(R.string.wind) + " " + speed?.roundToInt().toString())
        }
        weatherData.weather?.let {
            tvWeatherDetail.text = it[0].description
            if (NetworkWatcher.getInstance(applicationContext).isOnline)
                peopleListViewModel.getWeatherIcon(it[0].icon)
        }
    }

    private var searchItem: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        searchItem = menu!!.findItem(R.id.actionSearch)

        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (NetworkWatcher.getInstance(applicationContext).isOnline) {
            query?.run {
                peopleListViewModel.getLocationFromAddress(this, "b6329cc033c7a23d4fc04d0429f182cc")
            }
            searchItem?.collapseActionView()
        } else
            Toast.makeText(this@WeatherDashboardActivity, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}


