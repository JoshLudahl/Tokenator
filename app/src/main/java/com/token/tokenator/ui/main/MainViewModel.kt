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
import com.token.tokenator.di.DataStoreSortOrder
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
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

enum class TokenSortOrder {
    DATE,
    NAME,
}

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
        @DataStoreSortOrder private var sortOrderKey: String,
    ) : ViewModel() {
        var version: String
        val token: StateFlow<String>
            field = MutableStateFlow<String>("")

        val tokenNameEditTextLabelVisibility: StateFlow<Boolean>
            field = MutableStateFlow(false)

        val tokenNameEditTextFieldVisibility: StateFlow<Boolean>
            field = MutableStateFlow(false)

        val allCharacters: StateFlow<List<SettingsItem>>
            field = MutableStateFlow<List<SettingsItem>>(emptyList())

        val switchLowerCase: StateFlow<Boolean>
            field = MutableStateFlow(true)

        val switchNumeric: StateFlow<Boolean>
            field = MutableStateFlow(true)

        val switchSpecialCharacter: StateFlow<Boolean>
            field = MutableStateFlow(true)

        val switchUpperCase: StateFlow<Boolean>
            field = MutableStateFlow(true)

        val noRepeatFlow: Flow<Boolean> =
            dataStore.data.map { preferences ->
                (preferences[stringPreferencesKey(noRepeat)] ?: true).toString().toBoolean()
            }

        val passphrase: StateFlow<Passphrase?>
            field = MutableStateFlow<Passphrase?>(null)

        val switchPassphrase: StateFlow<Boolean>
            field = MutableStateFlow(true)

        val switchPassphraseFlow: Flow<Boolean> =
            dataStore.data.map { preferences ->
                (preferences[stringPreferencesKey(includePassphrase)] ?: true).toString().toBoolean()
            }

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> get() = _searchQuery

        private val _sortOrder = MutableStateFlow(TokenSortOrder.NAME)
        val sortOrder: StateFlow<TokenSortOrder> get() = _sortOrder

        private val _snackbarMessage = MutableStateFlow<Int?>(null)
        val snackbarMessage: StateFlow<Int?> get() = _snackbarMessage

        val allTokens: StateFlow<List<Token>> =
            repository.allTokensFlow
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val tokens: StateFlow<List<Token>> =
            combine(allTokens, searchQuery, _sortOrder) { list, query, order ->
                val filtered =
                    if (query.isBlank()) {
                        list
                    } else {
                        list.filter { token ->
                            token.title.contains(query, ignoreCase = true) ||
                                (token.login?.let { Encryption.decrypt(it) }?.contains(query, ignoreCase = true) == true)
                        }
                    }

                when (order) {
                    TokenSortOrder.NAME -> filtered.sortedBy { it.title.lowercase() }
                    TokenSortOrder.DATE ->
                        filtered.sortedByDescending {
                            try {
                                val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                                sdf.parse(it.date)?.time ?: 0L
                            } catch (e: Exception) {
                                Log.i("Error:", e.message.toString())
                                0L
                            }
                        }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun setSearchQuery(query: String) {
            _searchQuery.value = query
        }

        fun setSortOrder(order: TokenSortOrder) {
            _sortOrder.value = order
            viewModelScope.launch {
                DataPref.saveDataStore(sortOrderKey, order.name, dataStore)
            }
        }

        fun showSnackbar(resId: Int) {
            _snackbarMessage.value = resId
        }

        fun clearSnackbar() {
            _snackbarMessage.value = null
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
            tokenNameEditTextLabelVisibility.value = false
            tokenNameEditTextFieldVisibility.value = false

            // set switches
            viewModelScope.launch {
                repository.passphraseflow.collectLatest {
                    passphrase.value = it
                }
            }

            viewModelScope.launch {
                switchLowerCase.value =
                    (DataPref.readDataStore(lowercase, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                switchNumeric.value =
                    (DataPref.readDataStore(numeric, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                switchSpecialCharacter.value =
                    (DataPref.readDataStore(specialCharacters, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                switchUpperCase.value =
                    (DataPref.readDataStore(uppercase, dataStore) ?: true)
                        .toString()
                        .toBoolean()
            }

            viewModelScope.launch {
                switchPassphraseFlow.collectLatest {
                    switchPassphrase.value = it
                }
            }

            viewModelScope.launch {
                settingsItemRepository.allCharacters.collect { characters ->
                    allCharacters.value = characters
                }
            }

            viewModelScope.launch {
                DataPref.readDataStore(sortOrderKey, dataStore)?.let { savedOrder ->
                    try {
                        _sortOrder.value = TokenSortOrder.valueOf(savedOrder)
                    } catch (e: Exception) {
                        Log.i("Error:", e.message.toString())
                        _sortOrder.value = TokenSortOrder.NAME
                    }
                }
            }
        }

        val length: StateFlow<Float>
            field = MutableStateFlow<Float>(0f)

        fun setToken(text: String) {
            token.value = text
        }

        fun setLength(value: Float) {
            length.value = value
        }

        fun saveSwitchState(
            type: Type,
            checked: Boolean,
        ) {
            viewModelScope.launch {
                when (type) {
                    Type.LOWERCASE -> {
                        switchLowerCase.value = checked
                        lowercase
                    }

                    Type.NUMERIC -> {
                        switchNumeric.value = checked
                        numeric
                    }

                    Type.UPPERCASE -> {
                        switchUpperCase.value = checked
                        uppercase
                    }

                    Type.SPECIAL -> {
                        switchSpecialCharacter.value = checked
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
