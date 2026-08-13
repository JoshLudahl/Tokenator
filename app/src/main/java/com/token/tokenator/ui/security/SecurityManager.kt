package com.token.tokenator.ui.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.token.tokenator.utilities.DataPref
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private val _isAuthenticated = MutableStateFlow(false)
        val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

        private val _isSecurityEnabled = MutableStateFlow(false)
        val isSecurityEnabled: StateFlow<Boolean> = _isSecurityEnabled

        private val scope = MainScope()

        init {
            scope.launch {
                val enabled =
                    (DataPref.readDataStore("security_enabled", dataStore) ?: false)
                        .toString()
                        .toBoolean()
                _isSecurityEnabled.value = enabled
                if (!enabled) {
                    _isAuthenticated.value = true
                }
            }
        }

        fun setSecurityEnabled(enabled: Boolean) {
            scope.launch {
                DataPref.saveDataStore("security_enabled", enabled, dataStore)
                _isSecurityEnabled.value = enabled
                if (!enabled) {
                    _isAuthenticated.value = true
                }
            }
        }

        fun setAuthenticated(authenticated: Boolean) {
            _isAuthenticated.value = authenticated
        }

        fun resetAuthentication() {
            if (_isSecurityEnabled.value) {
                _isAuthenticated.value = false
            }
        }
    }
