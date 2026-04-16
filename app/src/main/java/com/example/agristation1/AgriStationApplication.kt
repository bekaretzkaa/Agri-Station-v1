package com.example.agristation1

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.agristation1.data.AppContainer
import com.example.agristation1.data.DefaultAppContainer
import com.example.agristation1.data.UserPreferencesRepository

private val IS_LIGHT_THEME = booleanPreferencesKey("is_light_theme")
private val LAST_SYNC = longPreferencesKey("last_sync")

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

class AgriStationApplication : Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}