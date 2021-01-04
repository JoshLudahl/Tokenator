package com.token.tokenator.main

import android.text.Editable
import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.BuildConfig
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private var repository: TokenRepository) : ViewModel(), LifecycleObserver{


    var version: String

    init {
        Log.i("MainViewModel", "Initialized")
        version = "Version ${BuildConfig.VERSION_NAME}"
        Log.i("VERSION", version)
    }

    var token: String = ""
    var tokenLength: Editable? = null
    var tokenNameEditText = View.GONE

    fun insert(
        passwordName: String,
        token: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(Token(title = passwordName, token = token))
            Log.i("DATABASE", "Saved to database")
        }
    }
}
