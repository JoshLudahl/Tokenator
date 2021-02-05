package com.token.tokenator.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.token.tokenator.database.settingsitem.SettingsItemRepository

class SettingsViewModel @ViewModelInject constructor(
    private val repository: SettingsItemRepository
    ) : ViewModel(), LifecycleObserver {

}