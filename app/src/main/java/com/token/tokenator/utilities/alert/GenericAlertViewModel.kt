package com.token.tokenator.utilities.alert

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GenericAlertViewModel(val title: Int) : ViewModel(), LifecycleObserver {

    private var _titleText: Int = title

    private val _shouldDismiss = MutableStateFlow(false)

    val shouldDismiss: StateFlow<Boolean>
        get() = _shouldDismiss

    val titleText
        get() = _titleText

    fun dismissDialog() {
        _shouldDismiss.value = true
    }
}
