package com.token.tokenator.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.*
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.di.DataStorePassPhraseIncluded
import com.token.tokenator.model.Passphrase
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.utilities.DataPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsItemRepository,
    private val tokenRepository: TokenRepository,
    private val dataStore: DataStore<Preferences>,
    @DataStorePassPhraseIncluded private val includePassPhrase: String
) : ViewModel(), LifecycleObserver {

    val specialCharList: LiveData<List<SettingsItem>> = repository.allCharacters.asLiveData()
    val numericCharList: LiveData<List<SettingsItem>> = repository.allNumericChars
    val lowerCaseCharList: LiveData<List<SettingsItem>> = repository.allLowerCaseChars
    val upperCaseCharList: LiveData<List<SettingsItem>> = repository.allUpperCaseChars
    val passphrase: LiveData<Passphrase>? = tokenRepository.passphrase

    private val _switchPassphrase = MutableStateFlow(true)
    val switchPassphrase: StateFlow<Boolean>
        get() = _switchPassphrase

    init {
        viewModelScope.launch {
            _switchPassphrase.value = (DataPref.readDataStore(includePassPhrase, dataStore) ?: true)
                .toString()
                .toBoolean()
        }
    }

    fun updateItems(settingsItem: SettingsItem) {
        viewModelScope.launch {
            repository.update(settingsItem)
        }
    }

    fun insertPassphrase(passphrase: Passphrase) {
        viewModelScope.launch {
            tokenRepository.insertPassphrase(passphrase = passphrase)
        }
    }

    fun updatePassphrase(checked: Boolean) {
        viewModelScope.launch {
            DataPref.saveDataStore(includePassPhrase, checked, dataStore)
        }
    }

    fun togglePassphraseSwitch() {
        _switchPassphrase.value = !switchPassphrase.value
    }
}
