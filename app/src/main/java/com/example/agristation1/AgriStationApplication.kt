package com.example.agristation1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AgriStationApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}