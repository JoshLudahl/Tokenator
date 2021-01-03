package com.token.tokenator.main

import android.app.Application
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.BuildConfig

import com.token.tokenator.database.TokenDatabase
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TokenRepository
    var version: String

    init {
        val tokenDao = TokenDatabase.getDatabase(application).tokenDao()
        repository = TokenRepository(tokenDao)
        Log.i("MainViewModel", "Initialized")
        version = "Version ${BuildConfig.VERSION_NAME}"
        Log.i("VERSION", "$version")
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
