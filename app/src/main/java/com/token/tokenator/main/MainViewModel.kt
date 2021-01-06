package com.token.tokenator.main

import android.text.Editable
import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.token.tokenator.BuildConfig
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private var repository: TokenRepository) : ViewModel(), LifecycleObserver{


    var version: String
    private val _token = MutableLiveData<String>()

    init {
        Log.i("MainViewModel", "Initialized")
        version = "Version ${BuildConfig.VERSION_NAME}"
        Log.i("VERSION", version)
    }

    val token: LiveData<String>
        get() = _token

    fun setToken(text: String) {
        _token.value = text
    }

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
