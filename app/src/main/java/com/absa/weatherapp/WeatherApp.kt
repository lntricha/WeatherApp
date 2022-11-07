package com.absa.weatherapp

import android.app.Application
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherApp : Application() {
    //    @DelicateCoroutinesApi
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            NetworkWatcher.getInstance(this@WeatherApp).watchNetwork().collect {
                Log.d("Network Connected:", it.toString())
            }
        }
    }
}
