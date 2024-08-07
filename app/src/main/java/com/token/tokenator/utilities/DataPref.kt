package com.token.tokenator.utilities

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

object DataPref {
    suspend fun readDataStore(
        key: String,
        dataStore: DataStore<Preferences>,
    ): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun saveDataStore(
        key: String,
        value: Boolean,
        dataStore: DataStore<Preferences>,
    ) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = value.toString()
        }
    }
}
