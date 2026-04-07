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
        val LATEST_VERSION_TAG = stringPreferencesKey("latest_version_tag")
        val LATEST_VERSION_URL = stringPreferencesKey("latest_version_url")
        val DISMISSED_VERSION = stringPreferencesKey("dismissed_version")
    }

    val updateInfoFlow: Flow<Pair<String?, String?>> = context.dataStore.data
        .map { preferences ->
            preferences[LATEST_VERSION_TAG] to preferences[LATEST_VERSION_URL]
        }

    val dismissedVersionFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[DISMISSED_VERSION]
        }

    suspend fun saveUpdateInfo(tag: String, url: String) {
        context.dataStore.edit { preferences ->
            preferences[LATEST_VERSION_TAG] = tag
            preferences[LATEST_VERSION_URL] = url
        }
    }

    suspend fun clearUpdateInfo() {
        context.dataStore.edit { preferences ->
            preferences.remove(LATEST_VERSION_TAG)
            preferences.remove(LATEST_VERSION_URL)
        }
    }

    suspend fun saveDismissedVersion(tag: String) {
        context.dataStore.edit { preferences ->
            preferences[DISMISSED_VERSION] = tag
        }
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
