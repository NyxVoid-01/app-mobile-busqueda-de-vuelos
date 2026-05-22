package com.example.bsquedadevuelos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val SEARCH_PREFERENCES_NAME = "search_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SEARCH_PREFERENCES_NAME
)

interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val flightSearchRepository: FlightSearchRepository by lazy {
        OfflineFlightSearchRepository(FlightSearchDatabase.getDatabase(context).flightSearchDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}
