package com.token.tokenator.ui.passworddetail

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.di.DataStoreLowercase
import com.token.tokenator.di.DataStoreNoRepeat
import com.token.tokenator.di.DataStoreNumeric
import com.token.tokenator.di.DataStorePassPhraseIncluded
import com.token.tokenator.di.DataStoreSpecialCharacters
import com.token.tokenator.di.DataStoreUppercase
import com.token.tokenator.model.Token
import com.token.tokenator.model.Type
import com.token.tokenator.utilities.DataPref
import com.token.tokenator.utilities.Encryption
import com.token.tokenator.utilities.TOKENATOR_TAG
import com.token.tokenator.utilities.Tokenator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
        private val settingsItemRepository: SettingsItemRepository,
        private val dataStore: DataStore<Preferences>,
        @DataStoreNoRepeat private val noRepeatKey: String,
        @DataStoreLowercase private val lowercaseKey: String,
        @DataStoreNumeric private val numericKey: String,
        @DataStoreSpecialCharacters private val specialCharactersKey: String,
        @DataStoreUppercase private val uppercaseKey: String,
        @DataStorePassPhraseIncluded private val includePassphraseKey: String,
    ) : ViewModel() {
        val token: StateFlow<Token?>
            field = MutableStateFlow<Token?>(null)

        val shouldShowWarning: StateFlow<Boolean>
            field = MutableStateFlow(false)

        private val _updateStatus = MutableSharedFlow<Boolean>()
        val updateStatus: SharedFlow<Boolean> = _updateStatus

        private val _generatedPassword = MutableSharedFlow<String>()
        val generatedPassword: SharedFlow<String> = _generatedPassword

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
                this@PasswordDetailViewModel.token.value = token
                val isOld = isOldPassword(newToken?.date ?: Date().toString())
                shouldShowWarning.value = isOld || token.token.isEmpty()
                Log.i(TOKENATOR_TAG, "DATE: ${this@PasswordDetailViewModel.token.value?.date}")
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

        fun updateToken(
            passwordName: String,
            tokenValue: String,
            login: String? = null,
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val encryptedToken = Encryption.encrypt(tokenValue)
                    val encryptedLogin =
                        login?.trim()?.let {
                            if (it.isNotEmpty()) {
                                Encryption.encrypt(it)
                            } else {
                                null
                            }
                        }

                    if (encryptedToken != null) {
                        val currentToken = token.value
                        if (currentToken != null) {
                            val date = Date().toString()
                            val updatedToken =
                                currentToken.copy(
                                    title = passwordName,
                                    token = encryptedToken,
                                    login = encryptedLogin,
                                    date = date,
                                )
                            tokenRepository.updateToken(updatedToken)

                            token.value =
                                currentToken.copy(
                                    title = passwordName,
                                    token = tokenValue,
                                    login = login,
                                    date = date,
                                )
                            shouldShowWarning.value = isOldPassword(date)
                            _updateStatus.emit(true)
                            Log.i("DATABASE", "Saved to database")
                        }
                    } else {
                        _updateStatus.emit(false)
                        Log.e(TOKENATOR_TAG, "Encryption failed")
                    }
                } catch (e: Exception) {
                    Log.e("Error:", e.message.toString())
                    _updateStatus.emit(false)
                }
            }
        }

        fun generateNewPassword(length: Int = 16) {
            viewModelScope.launch {
                val types = mutableListOf<Type>()
                if (DataPref.readDataStore(uppercaseKey, dataStore)?.toBoolean() ?: true) {
                    types.add(
                        Type.UPPERCASE,
                    )
                }
                if (DataPref.readDataStore(lowercaseKey, dataStore)?.toBoolean() ?: true) {
                    types.add(
                        Type.LOWERCASE,
                    )
                }
                if (DataPref.readDataStore(numericKey, dataStore)?.toBoolean() ?: true) {
                    types.add(
                        Type.NUMERIC,
                    )
                }
                if (DataPref.readDataStore(specialCharactersKey, dataStore)?.toBoolean() ?: true) {
                    types.add(
                        Type.SPECIAL,
                    )
                }

                val allCharacters = settingsItemRepository.allCharacters.first()
                val excluded = allCharacters.filter { !it.included }.map { it.item }
                val noRepeat = (DataPref.readDataStore(noRepeatKey, dataStore)?.toBoolean() ?: true)
                val switchPassphrase = (DataPref.readDataStore(includePassphraseKey, dataStore)?.toBoolean() ?: true)
                val passphrase = tokenRepository.passphraseflow.first()

                val generated =
                    Tokenator.generate(
                        length = length,
                        includesTypesList = types,
                        excludedCharacters = excluded,
                        doNotRepeat = noRepeat,
                        includePhrase = if (switchPassphrase) passphrase?.phrase ?: "" else "",
                    )

                if (generated.isNotEmpty()) {
                    token.value?.let {
                        updateToken(
                            passwordName = it.title,
                            tokenValue = generated,
                            login = it.login,
                        )
                    }
                }
            }
        }

        fun deleteToken(token: Token) {
            viewModelScope.launch(Dispatchers.IO) {
                tokenRepository.delete(token)
            }
        }
    }
