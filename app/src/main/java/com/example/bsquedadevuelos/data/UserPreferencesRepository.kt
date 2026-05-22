package com.example.bsquedadevuelos.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val SEARCH_TEXT = stringPreferencesKey("search_text")
        const val TAG = "UserPreferencesRepo"
    }

    val searchText: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SEARCH_TEXT] ?: ""
        }

    suspend fun saveSearchText(searchText: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_TEXT] = searchText
        }
    }
}
