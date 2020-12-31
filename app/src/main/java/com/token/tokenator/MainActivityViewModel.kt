package com.token.tokenator

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    var token: String = ""
    var tokenLength: Editable? = null

    fun updateTokenLength(tokenLength: Editable?) {
        this.tokenLength = tokenLength
    }
}
