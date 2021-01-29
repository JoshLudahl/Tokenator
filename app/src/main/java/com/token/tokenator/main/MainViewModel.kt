package com.token.tokenator.main

import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.token.tokenator.BuildConfig
import com.token.tokenator.Utilities.Encryption
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private var repository: TokenRepository) :
    ViewModel(), LifecycleObserver {

    var version: String
    private val _token = MutableLiveData<String>()
    private val _length = MutableLiveData<Float>()
    private val _tokenNameEditTextLabelVisibility = MutableLiveData<Int>()
    private val _tokenNameEditTextFieldVisibility = MutableLiveData<Int>()

    init {
        Log.i("MainViewModel", "Initialized")
        version = "Version ${BuildConfig.VERSION_NAME}"
        Log.i("VERSION", version)
        _tokenNameEditTextLabelVisibility.value = View.GONE
        _tokenNameEditTextFieldVisibility.value = View.GONE
    }

    val token: LiveData<String>
        get() = _token

    val length: LiveData<Float>
        get() = _length

    fun setToken(text: String) {
        _token.value = text
    }

    fun setLength(value: Float) {
        _length.value = value
    }

    var tokenNameEditText = View.GONE

    val tokenNameEditTextFieldVisibility : LiveData<Int>
        get() = _tokenNameEditTextFieldVisibility

    fun setTokenNameEditTextFieldVisibility() =
        _tokenNameEditTextFieldVisibility.postValue(View.VISIBLE)

    val tokenNameEditTextLabelVisibility : LiveData<Int>
        get() = _tokenNameEditTextLabelVisibility

    fun setTokenNameEditTextLabelVisible() =
        _tokenNameEditTextLabelVisibility.postValue(View.VISIBLE)

    fun insert(
        passwordName: String,
        token: String
    ) {
        try {
            val encryptedName = Encryption.encrypt(passwordName) ?: "No name"
            val encryptedToken = Encryption.encrypt(token)

            encryptedToken?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insert(Token(title = passwordName, token = it))
                    Log.i("DATABASE", "Saved to database")
                }
            }
        } catch (e: Exception) {
            Log.i("Error:", e.message.toString())
        }
    }
}
