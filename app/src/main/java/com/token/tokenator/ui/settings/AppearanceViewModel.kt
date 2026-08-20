package com.token.tokenator.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.di.DataStoreDynamicColor
import com.token.tokenator.di.DataStoreThemeMode
import com.token.tokenator.model.ThemeMode
import com.token.tokenator.utilities.DataPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        @DataStoreThemeMode private val themeModeKey: String,
        @DataStoreDynamicColor private val dynamicColorKey: String,
    ) : ViewModel() {
        val themeMode: StateFlow<ThemeMode> =
            dataStore.data
                .map { preferences ->
                    preferences[stringPreferencesKey(themeModeKey)]?.let {
                        runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM)
                    } ?: ThemeMode.SYSTEM
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

        val dynamicColor: StateFlow<Boolean> =
            dataStore.data
                .map { preferences ->
                    preferences[stringPreferencesKey(dynamicColorKey)]?.toBoolean() ?: true
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

        fun setThemeMode(mode: ThemeMode) {
            viewModelScope.launch {
                DataPref.saveDataStore(themeModeKey, mode.name, dataStore)
            }
        }

        fun setDynamicColor(enabled: Boolean) {
            viewModelScope.launch {
                DataPref.saveDataStore(dynamicColorKey, enabled, dataStore)
            }
        }
    }
