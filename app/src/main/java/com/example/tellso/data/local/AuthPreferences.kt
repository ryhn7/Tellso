package com.example.tellso.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {

    fun getAuthToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_AUTH]
        }
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH] = token
        }
    }

    companion object {
        private val KEY_AUTH = stringPreferencesKey("auth_token")
    }
}