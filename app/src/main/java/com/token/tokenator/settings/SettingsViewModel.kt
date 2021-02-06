package com.token.tokenator.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
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