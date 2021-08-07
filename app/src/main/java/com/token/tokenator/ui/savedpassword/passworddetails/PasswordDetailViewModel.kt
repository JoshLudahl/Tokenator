package com.token.tokenator.ui.savedpassword.passworddetails

import android.util.Log
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class PasswordDetailViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _token = MutableLiveData<Token>()
    val token: LiveData<Token>
        get() = _token

    private val _shouldShowWarning = MutableLiveData<Int>()
    val shouldShowWarning: LiveData<Int>
        get() = _shouldShowWarning

    init {
        _shouldShowWarning.value = View.GONE
    }

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
                token = newToken?.token?.let { Encryption.decrypt(it) } ?: "",
                date = newToken?.date ?: Date().toString()
            )
            _token.value = token

            _shouldShowWarning.value = if (isOldPassword(newToken?.date ?: Date().toString())) View.VISIBLE else View.GONE
            Log.i("DATE", _token.value?.date.toString())
        }
    }

    private fun isOldPassword(date: String): Boolean {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val firstDate: Date = sdf.parse(date) ?: Date()
        val secondDate = Date()
        val diffInMillies = abs(secondDate.time - firstDate.time)

        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) > 90
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
                    _token.value?.apply {
                        date = Date().toString()
                        this.token = encryptedToken
                    }

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
