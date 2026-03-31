package com.example.agristation1

import android.app.Application
import com.example.agristation1.data.AppContainer
import com.example.agristation1.data.DefaultAppContainer

class AgriStationApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}