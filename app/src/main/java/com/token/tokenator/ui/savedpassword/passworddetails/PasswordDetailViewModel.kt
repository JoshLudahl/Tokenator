package com.token.tokenator.ui.savedpassword.passworddetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.model.Token
import com.token.tokenator.utilities.Encryption
import com.token.tokenator.utilities.TOKENATOR_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class PasswordDetailViewModel
    @Inject
    constructor(
        private val tokenRepository: TokenRepository,
    ) : ViewModel() {
        private val _token = MutableStateFlow<Token?>(null)
        val token: StateFlow<Token?>
            get() = _token

        private val _shouldShowWarning = MutableStateFlow(false)
        val shouldShowWarning: StateFlow<Boolean>
            get() = _shouldShowWarning

        fun getToken(id: Int) {
            viewModelScope.launch {
                val newToken = tokenRepository.getOneTokenById(id)

                val token =
                    Token(
                        id = id,
                        title = newToken?.title ?: "",
                        login = newToken?.login?.let { Encryption.decrypt(it) } ?: "",
                        token = newToken?.token?.let { Encryption.decrypt(it) } ?: "",
                        date = newToken?.date ?: Date().toString(),
                    )
                _token.value = token

                _shouldShowWarning.value = isOldPassword(newToken?.date ?: Date().toString())
                Log.i(TOKENATOR_TAG, "DATE: ${_token.value?.date}")
            }
        }

        private fun isOldPassword(date: String): Boolean =
            try {
                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                val firstDate: Date = sdf.parse(date) ?: Date()
                val secondDate = Date()
                val diffInMillies = abs(secondDate.time - firstDate.time)
                TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) > 90
            } catch (e: Exception) {
                false
            }

        fun insert(
            passwordName: String,
            token: String,
            login: String? = null,
        ) {
            try {
                val encryptedToken = Encryption.encrypt(token)
                val encryptedLogin =
                    login?.trim()?.let {
                        if (it.isNotEmpty()) {
                            Encryption.encrypt(it)
                        } else {
                            null
                        }
                    }

                encryptedToken?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        val currentToken = _token.value
                        if (currentToken != null) {
                            val updatedToken =
                                currentToken.copy(
                                    title = passwordName,
                                    token = encryptedToken,
                                    login = encryptedLogin,
                                    date = Date().toString(),
                                )
                            tokenRepository.updateToken(updatedToken)
                            _token.value = updatedToken
                        }
                        Log.i("DATABASE", "Saved to database")
                    }
                }
            } catch (e: Exception) {
                Log.i("Error:", e.message.toString())
            }
        }
    }
