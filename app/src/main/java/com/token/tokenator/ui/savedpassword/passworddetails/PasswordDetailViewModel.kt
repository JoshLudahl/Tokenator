package com.token.tokenator.ui.savedpassword.passworddetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.model.Token
import com.token.tokenator.utilities.Encryption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordDetailViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _token = MutableLiveData<Token>()
    val token: LiveData<Token>
        get() = _token

    fun updateToken(token: Token) {
        viewModelScope.launch {
            tokenRepository.updateToken(token)
        }
    }

    fun getToken(id: Int) {
        viewModelScope.launch {
            val newToken = tokenRepository.getOneTokenById(id)
            val token = Token(
                id = id,
                title = newToken?.title ?: "",
                login = newToken?.login?.let { Encryption.decrypt(it) } ?: "",
                token = newToken?.token?.let { Encryption.decrypt(it) } ?: ""
            )
            _token.value = token
        }
    }

    fun insert(
        passwordName: String,
        token: String,
        login: String? = null
    ) {
        try {
            val encryptedName = Encryption.encrypt(passwordName) ?: "No name"
            val encryptedToken = Encryption.encrypt(token)
            val encryptedLogin = login?.trim()?.let {
                if (it.isNotEmpty()) {
                    Encryption.encrypt(it)
                } else {
                    null
                }
            }

            encryptedToken?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    _token.value?.title = passwordName
                   encryptedLogin?.let {
                       _token.value?.login = it
                   }
                    _token.value?.token = encryptedToken

                    _token.value?.let {
                        tokenRepository.updateToken(
                            it
                        )
                    }

                    Log.i("DATABASE", "Saved to database")
                }
            }
        } catch (e: Exception) {
            Log.i("Error:", e.message.toString())
        }
    }
}
