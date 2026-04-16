package com.example.agristation1.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    val isLightTheme: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e("UserPreferencesRepo", "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_LIGHT_THEME] ?: true
        }

    val lastSync: Flow<Long> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e("UserPreferencesRepo", "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_SYNC] ?: 0L
        }

    private companion object {
        val IS_LIGHT_THEME = booleanPreferencesKey("is_light_theme")
        val LAST_SYNC = longPreferencesKey("last_sync")
    }

    suspend fun saveIsLightTheme(isLightTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LIGHT_THEME] = isLightTheme
        }
    }

    suspend fun saveLastSync(lastSync: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC] = lastSync
        }
    }

}