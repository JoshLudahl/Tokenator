package com.token.tokenator.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.model.SettingsItem
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val repository: SettingsItemRepository
    ) : ViewModel(), LifecycleObserver {

    private val _specialCharList = MutableLiveData<List<SettingsItem>>()

    val specialCharList: LiveData<List<SettingsItem>>
        get() = _specialCharList

        init {
             viewModelScope.launch {
                  getAllSpecialChars()
             }
        }

    private fun getAllSpecialChars() {
        _specialCharList.postValue(repository.allSpecialChars.value)
    }
}