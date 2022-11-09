package com.token.tokenator.utilities.alert

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.token.tokenator.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GenericAlertViewModel : ViewModel(), LifecycleObserver {

    val _titleText = R.string.privacy_policy

    private val _shouldDismiss = MutableStateFlow(false)

    val shouldDismiss: StateFlow<Boolean>
        get() = _shouldDismiss

    val titleText
        get() = _titleText

    fun dismissDialog() {
        _shouldDismiss.value = true
    }
}
