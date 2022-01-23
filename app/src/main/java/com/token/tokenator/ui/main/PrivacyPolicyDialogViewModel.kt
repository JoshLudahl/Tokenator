package com.token.tokenator.ui.main

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.token.tokenator.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrivacyPolicyDialogViewModel : ViewModel(), LifecycleObserver {

    private val _shouldDismiss = MutableStateFlow(false)

    val shouldDismiss: StateFlow<Boolean>
        get() = _shouldDismiss

    private val _titleText = R.string.privacy_policy
    val titleText get() = _titleText

    fun dismissDialog() {
        _shouldDismiss.value = true
    }
}
