package com.token.tokenator.utilities.alert

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GenericAlertViewModel : ViewModel(), LifecycleObserver {
    private val _shouldDismiss = MutableStateFlow(false)

    val shouldDismiss: StateFlow<Boolean>
        get() = _shouldDismiss

    fun dismissDialog() {
        _shouldDismiss.value = true
    }
}
