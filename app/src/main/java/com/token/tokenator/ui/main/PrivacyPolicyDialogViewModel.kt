package com.token.tokenator.ui.main

import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrivacyPolicyDialogViewModel : ViewModel(), LifecycleObserver {

    private val _shouldDismiss = MutableStateFlow<Boolean>(false)

    val shouldDismiss: StateFlow<Boolean>
        get() = _shouldDismiss

    fun dismissDialog() {
        _shouldDismiss.value = true
    }
}
