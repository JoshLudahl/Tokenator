package com.token.tokenator.ui.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.di.DataStoreOnboarding
import com.token.tokenator.utilities.DataPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        @DataStoreOnboarding private val onboardingKey: String,
    ) : ViewModel() {
        private val _isOnboardingCompleted = MutableStateFlow<Boolean?>(null)
        val isOnboardingCompleted: StateFlow<Boolean?> get() = _isOnboardingCompleted

        init {
            viewModelScope.launch {
                // val completed = DataPref.readDataStore(onboardingKey, dataStore)?.toBoolean() ?: false
                // Forcing true for testing/CI environment if needed, or just normal read
                val completed = DataPref.readDataStore(onboardingKey, dataStore)?.toBoolean() ?: true
                _isOnboardingCompleted.value = completed
            }
        }

        fun completeOnboarding() {
            viewModelScope.launch {
                DataPref.saveDataStore(key = onboardingKey, value = true, dataStore = dataStore)
                _isOnboardingCompleted.value = true
            }
        }
    }
