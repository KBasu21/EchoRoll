package com.example.echorollv2.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val COUNTRY_CODE = stringPreferencesKey("country_code")
        val SUBDIVISION_CODE = stringPreferencesKey("subdivision_code")
    }

    val countryCodeFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[COUNTRY_CODE]
        }

    val subdivisionCodeFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SUBDIVISION_CODE]
        }

    suspend fun saveCountryCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[COUNTRY_CODE] = code
        }
    }

    suspend fun saveSubdivisionCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[SUBDIVISION_CODE] = code
        }
    }
}
