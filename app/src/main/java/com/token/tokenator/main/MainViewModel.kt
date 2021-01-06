package com.token.tokenator.main

import android.text.Editable
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.Bindable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.token.tokenator.BuildConfig
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(private var repository: TokenRepository) :
    ViewModel(), LifecycleObserver {

    var version: String
    private val _token = MutableLiveData<String>()
    private val _length = MutableLiveData<Editable?>()

    init {
        Log.i("MainViewModel", "Initialized")
        version = "Version ${BuildConfig.VERSION_NAME}"
        Log.i("VERSION", version)
        _length.value = null
    }

    val token: LiveData<String>
        get() = _token

    val length: LiveData<Editable?>
        get() = _length

    fun setToken(text: String) {
        _token.value = text
    }

    fun updateTokenLength(value: Editable?) {
        _length.value = value
    }

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
