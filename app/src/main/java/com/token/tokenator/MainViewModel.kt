package com.token.tokenator

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.di.DataStoreBiometric
import com.token.tokenator.utilities.DataPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        @DataStoreBiometric private val biometricKey: String,
    ) : ViewModel() {
        private val _isBiometricEnabled = MutableStateFlow<Boolean?>(null)
        val isBiometricEnabled: StateFlow<Boolean?> get() = _isBiometricEnabled

        init {
            viewModelScope.launch {
                val enabled = DataPref.readDataStore(biometricKey, dataStore)?.toBoolean() ?: false
                _isBiometricEnabled.value = enabled
            }
        }
    }
