package com.token.tokenator.settings

import androidx.lifecycle.*
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

    fun updateItems(settingsItem: SettingsItem) {
        viewModelScope.launch {
            repository.update(settingsItem)
        }
    }
}
