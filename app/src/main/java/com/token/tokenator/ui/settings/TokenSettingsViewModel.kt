package com.token.tokenator.ui.settings

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.BuildConfig
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
class TokenSettingsViewModel
    @Inject
    constructor(
        private val repository: SettingsItemRepository,
        private val tokenRepository: TokenRepository,
        private val dataStore: DataStore<Preferences>,
        @DataStorePassPhraseIncluded private val includePassPhrase: String,
        @DataStoreNoRepeat val noRepeatKey: String,
        @DataStoreBiometric private val biometricKey: String,
    ) : ViewModel() {
        val version = "Version ${BuildConfig.VERSION_NAME}"

        val allCharacters: StateFlow<List<SettingsItem>> =
            repository.allCharacters
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val passphrase: StateFlow<Passphrase?> =
            tokenRepository.passphraseflow
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        val switchPassphrase: StateFlow<Boolean>
            field = MutableStateFlow(true)

        val switchNoRepeat: StateFlow<Boolean>
            field = MutableStateFlow(true)

        val switchBiometric: StateFlow<Boolean>
            field = MutableStateFlow(false)

        init {
            viewModelScope.launch {
                switchPassphrase.value =
                    (DataPref.readDataStore(includePassPhrase, dataStore) ?: true)
                        .toString()
                        .toBoolean()

                switchNoRepeat.value =
                    (DataPref.readDataStore(noRepeatKey, dataStore) ?: true)
                        .toString()
                        .toBoolean()

                switchBiometric.value =
                    (DataPref.readDataStore(biometricKey, dataStore) ?: false)
                        .toString()
                        .toBoolean()
            }
        }

        fun updateItems(settingsItem: SettingsItem) {
            Log.d(
                "TokenSettingsViewModel",
                "Updating item: ${settingsItem.item}, included: ${settingsItem.included}, id: ${settingsItem.id}",
            )
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
            switchPassphrase.value = checked
            viewModelScope.launch {
                DataPref.saveDataStore(includePassPhrase, checked, dataStore)
            }
        }

        fun updateNoRepeat(checked: Boolean) {
            switchNoRepeat.value = checked
            viewModelScope.launch {
                DataPref.saveDataStore(noRepeatKey, checked, dataStore)
            }
        }

        fun updateBiometric(checked: Boolean) {
            switchBiometric.value = checked
            viewModelScope.launch {
                DataPref.saveDataStore(biometricKey, checked, dataStore)
            }
        }

        fun clearAllItems() {
            viewModelScope.launch {
                repository.deleteAllCharacters()
            }
        }
    }
