package com.example.bsquedadevuelos

import android.app.Application
import com.example.bsquedadevuelos.data.AppContainer
import com.example.bsquedadevuelos.data.DefaultAppContainer

class FlightSearchApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
