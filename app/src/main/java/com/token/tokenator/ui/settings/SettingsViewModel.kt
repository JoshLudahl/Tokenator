package com.token.tokenator.ui.settings

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.di.DataStoreBiometric
import com.token.tokenator.di.DataStoreNoRepeat
import com.token.tokenator.di.DataStorePassPhraseIncluded
import com.token.tokenator.model.Passphrase
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.utilities.DataPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repository: SettingsItemRepository,
        private val tokenRepository: TokenRepository,
        private val dataStore: DataStore<Preferences>,
        @DataStorePassPhraseIncluded private val includePassPhrase: String,
        @DataStoreNoRepeat val noRepeatKey: String,
        @DataStoreBiometric private val biometricKey: String,
    ) : ViewModel() {
        val allCharacters: StateFlow<List<SettingsItem>> =
            repository.allCharacters
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val passphrase: StateFlow<Passphrase?> =
            tokenRepository.passphraseflow
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        private val _switchPassphrase = MutableStateFlow(true)
        val switchPassphrase: StateFlow<Boolean>
            get() = _switchPassphrase

        private val _switchNoRepeat = MutableStateFlow(true)
        val switchNoRepeat: StateFlow<Boolean>
            get() = _switchNoRepeat

        private val _switchBiometric = MutableStateFlow(false)
        val switchBiometric: StateFlow<Boolean>
            get() = _switchBiometric

        init {
            viewModelScope.launch {
                _switchPassphrase.value =
                    (DataPref.readDataStore(includePassPhrase, dataStore) ?: true)
                        .toString()
                        .toBoolean()

                _switchNoRepeat.value =
                    (DataPref.readDataStore(noRepeatKey, dataStore) ?: true)
                        .toString()
                        .toBoolean()

                _switchBiometric.value =
                    (DataPref.readDataStore(biometricKey, dataStore) ?: false)
                        .toString()
                        .toBoolean()
            }
        }

        fun updateItems(settingsItem: SettingsItem) {
            Log.d("SettingsViewModel", "Updating item: ${settingsItem.item}, included: ${settingsItem.included}, id: ${settingsItem.id}")
            viewModelScope.launch {
                repository.update(settingsItem)
            }
        }

        fun insertPassphrase(phrase: String) {
            viewModelScope.launch {
                tokenRepository.insertPassphrase(Passphrase(phrase = phrase))
            }
        }

        fun updatePassphrase(checked: Boolean) {
            _switchPassphrase.value = checked
            viewModelScope.launch {
                DataPref.saveDataStore(includePassPhrase, checked, dataStore)
            }
        }

        fun updateNoRepeat(checked: Boolean) {
            _switchNoRepeat.value = checked
            viewModelScope.launch {
                DataPref.saveDataStore(noRepeatKey, checked, dataStore)
            }
        }

        fun updateBiometric(checked: Boolean) {
            _switchBiometric.value = checked
            viewModelScope.launch {
                DataPref.saveDataStore(biometricKey, checked, dataStore)
            }
        }
    }
