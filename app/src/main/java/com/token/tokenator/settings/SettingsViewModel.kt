package com.token.tokenator.settings

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.model.SettingsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsItemRepository
) : ViewModel(), LifecycleObserver {

    val specialCharList: LiveData<List<SettingsItem>> = repository.allCharacters
    val numericCharList: LiveData<List<SettingsItem>> = repository.allNumericChars
    val lowerCaseCharList: LiveData<List<SettingsItem>> = repository.allLowerCaseChars
    val upperCaseCharList: LiveData<List<SettingsItem>> = repository.allUpperCaseChars

    fun updateItems(settingsItem: SettingsItem) {
        viewModelScope.launch {
            repository.update(settingsItem)
        }
    }
}
