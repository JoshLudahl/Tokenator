package com.token.tokenator.settings

import android.util.Log
import androidx.lifecycle.*
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.model.Passphrase
import com.token.tokenator.model.SettingsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsItemRepository,
    private val tokenRepository: TokenRepository
) : ViewModel(), LifecycleObserver {

    val specialCharList: LiveData<List<SettingsItem>> = repository.allCharacters
    val numericCharList: LiveData<List<SettingsItem>> = repository.allNumericChars
    val lowerCaseCharList: LiveData<List<SettingsItem>> = repository.allLowerCaseChars
    val upperCaseCharList: LiveData<List<SettingsItem>> = repository.allUpperCaseChars
    val passphrase: LiveData<Passphrase>? = tokenRepository.passphrase

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
}
