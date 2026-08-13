package com.token.tokenator.ui.main

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.BuildConfig
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.di.DataStoreLowercase
import com.token.tokenator.di.DataStoreNoRepeat
import com.token.tokenator.di.DataStoreNumeric
import com.token.tokenator.di.DataStorePassPhraseIncluded
import com.token.tokenator.di.DataStoreSpecialCharacters
import com.token.tokenator.di.DataStoreUppercase
import com.token.tokenator.model.Passphrase
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Token
import com.token.tokenator.model.Type
import com.token.tokenator.utilities.DataPref
import com.token.tokenator.utilities.Encryption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(
        private var repository: TokenRepository,
        settingsItemRepository: SettingsItemRepository,
        private var dataStore: DataStore<Preferences>,
        @DataStoreNoRepeat private var noRepeat: String,
        @DataStoreLowercase private var lowercase: String,
        @DataStoreNumeric private var numeric: String,
        @DataStoreSpecialCharacters var specialCharacters: String,
        @DataStoreUppercase var uppercase: String,
        @DataStorePassPhraseIncluded var includePassphrase: String,
    ) : ViewModel() {
        var version: String
        private val _token = MutableStateFlow<String>("")
        val token: StateFlow<String>
            get() = _token

        private val _length = MutableStateFlow<Float>(0f)

        private val _tokenNameEditTextLabelVisibility = MutableStateFlow(false)
        val tokenNameEditTextLabelVisibility: StateFlow<Boolean>
            get() = _tokenNameEditTextLabelVisibility

        private val _tokenNameEditTextFieldVisibility = MutableStateFlow(false)
        val tokenNameEditTextFieldVisibility: StateFlow<Boolean>
            get() = _tokenNameEditTextFieldVisibility

        private val _allCharacters = MutableStateFlow<List<SettingsItem>>(emptyList())
        val allCharacters: StateFlow<List<SettingsItem>>
            get() = _allCharacters

        private val _shouldShowEasterEggToast = MutableStateFlow<Boolean>(false)

        private val _switchLowerCase = MutableStateFlow(true)
        val switchLowerCase: StateFlow<Boolean>
            get() = _switchLowerCase

        private val _switchNumeric = MutableStateFlow(true)
        val switchNumeric: StateFlow<Boolean>
            get() = _switchNumeric

        private val _switchSpecialCharacter = MutableStateFlow(true)
        val switchSpecialCharacter: StateFlow<Boolean>
            get() = _switchSpecialCharacter

        private val _switchUpperCase = MutableStateFlow(true)
        val switchUpperCase: StateFlow<Boolean>
            get() = _switchUpperCase

        val noRepeatFlow: Flow<Boolean> =
            dataStore.data.map { preferences ->
                (preferences[stringPreferencesKey(noRepeat)] ?: true).toString().toBoolean()
            }

        private val _passphrase = MutableStateFlow<Passphrase?>(null)
        val passphrase: StateFlow<Passphrase?>
            get() = _passphrase

        private val _switchPassphrase = MutableStateFlow(true)
        val switchPassphrase: StateFlow<Boolean>
            get() = _switchPassphrase

        val switchPassphraseFlow: Flow<Boolean> =
            dataStore.data.map { preferences ->
                (preferences[stringPreferencesKey(includePassphrase)] ?: true).toString().toBoolean()
            }

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> get() = _searchQuery

        val allTokens: StateFlow<List<Token>> =
            repository.allTokensFlow
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val tokens: StateFlow<List<Token>> =
            combine(allTokens, searchQuery) { list, query ->
                if (query.isBlank()) {
                    list
                } else {
                    list.filter { token ->
                        token.title.contains(query, ignoreCase = true) ||
                            (token.login?.let { Encryption.decrypt(it) }?.contains(query, ignoreCase = true) == true)
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun setSearchQuery(query: String) {
            _searchQuery.value = query
        }

        fun delete(token: Token) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.delete(token)
            }
        }

        init {
            Log.i("MainViewModel", "Initialized")
            version = "Version ${BuildConfig.VERSION_NAME}"
            Log.i("VERSION", version)
            _tokenNameEditTextLabelVisibility.value = false
            _tokenNameEditTextFieldVisibility.value = false

            // set switches
            viewModelScope.launch {
                repository.passphraseflow?.collectLatest {
                    _passphrase.value = it
                }
            }

            viewModelScope.launch {
                _switchLowerCase.value =
                    (DataPref.readDataStore(lowercase, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                _switchNumeric.value =
                    (DataPref.readDataStore(numeric, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                _switchSpecialCharacter.value =
                    (DataPref.readDataStore(specialCharacters, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                _switchUpperCase.value =
                    (DataPref.readDataStore(uppercase, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                switchPassphraseFlow.collectLatest {
                    _switchPassphrase.value = it
                }
            }

            viewModelScope.launch {
                settingsItemRepository.allCharacters.collect { characters ->
                    _allCharacters.value = characters
                }
            }
        }

        val length: StateFlow<Float>
            get() = _length

        val shouldShowEasterEggToast: StateFlow<Boolean>
            get() = _shouldShowEasterEggToast

        fun setToken(text: String) {
            _token.value = text
        }

        fun setLength(value: Float) {
            _length.value = value
        }

        fun setTokenNameEditTextFieldVisibility() {
            _tokenNameEditTextFieldVisibility.value = true
        }

        fun setTokenNameEditTextLabelVisible() {
            _tokenNameEditTextLabelVisibility.value = true
        }

        fun showEasterEggToast() {
            _shouldShowEasterEggToast.value = true
        }

        fun setShouldShowToastToFalse() {
            _shouldShowEasterEggToast.value = false
        }

        fun saveSwitchState(
            type: Type,
            checked: Boolean,
        ) {
            viewModelScope.launch {
                when (type) {
                    Type.LOWERCASE -> {
                        _switchLowerCase.value = checked
                        lowercase
                    }

                    Type.NUMERIC -> {
                        _switchNumeric.value = checked
                        numeric
                    }

                    Type.UPPERCASE -> {
                        _switchUpperCase.value = checked
                        uppercase
                    }

                    Type.SPECIAL -> {
                        _switchSpecialCharacter.value = checked
                        specialCharacters
                    }
                }.let {
                    DataPref.saveDataStore(it, checked, dataStore)
                }
            }
        }

        fun insert(
            passwordName: String,
            token: String,
            login: String? = null,
        ) {
            try {
                // val encryptedName = Encryption.encrypt(passwordName) ?: "No name"
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
                        repository.insert(
                            Token(
                                title = passwordName,
                                token = it,
                                login = encryptedLogin,
                            ),
                        )
                        Log.i("DATABASE", "Saved to database")
                    }
                }
            } catch (e: Exception) {
                Log.i("Error:", e.message.toString())
            }
        }
    }
