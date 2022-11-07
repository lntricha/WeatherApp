package com.absa.weatherapp

import android.app.Application
import android.util.Log
import com.absa.weatherapp.utill.NetworkWatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherApp : Application() {

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            NetworkWatcher.getInstance(this@WeatherApp).watchNetwork().collect {
            }
        }
    }
}
