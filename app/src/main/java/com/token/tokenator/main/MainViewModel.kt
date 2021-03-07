package com.token.tokenator.main

import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.*
import com.token.tokenator.BuildConfig
import com.token.tokenator.utilities.DataPref
import com.token.tokenator.utilities.Encryption
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.di.*
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Token
import com.token.tokenator.model.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var repository: TokenRepository,
    settingsItemRepository: SettingsItemRepository,
    private var dataStore: DataStore<Preferences>,
    @DataStoreNoRepeat private var noRepeat: String,
    @DataStoreLowercase private var lowercase: String,
    @DataStoreNumeric private var numeric: String,
    @DataStoreSpecialCharacters var specialCharacters: String,
    @DataStoreUppercase var uppercase: String
) :
    ViewModel(), LifecycleObserver {

    var version: String
    private val _token = MutableStateFlow<String>("")
    private val _length = MutableStateFlow<Float>(0f)
    private val _tokenNameEditTextLabelVisibility = MutableLiveData<Int>()
    private val _tokenNameEditTextFieldVisibility = MutableLiveData<Int>()
    private val _allCharacters = settingsItemRepository.allCharacters
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
    val switchUpperCase: LiveData<Boolean>
        get() = _switchUpperCase.asLiveData()

    val noRepeatFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        (preferences[stringPreferencesKey(noRepeat)] ?: true).toString().toBoolean()
    }

    init {
        Log.i("MainViewModel", "Initialized")
        version = "Version ${BuildConfig.VERSION_NAME}"
        Log.i("VERSION", version)
        _tokenNameEditTextLabelVisibility.value = View.GONE
        _tokenNameEditTextFieldVisibility.value = View.GONE

        //set switches
        viewModelScope.launch {

            _switchLowerCase.value = (DataPref.readDataStore(lowercase, dataStore) ?: true)
                .toString()
                .toBoolean()

            _switchNumeric.value = (DataPref.readDataStore(numeric, dataStore) ?: true)
                .toString()
                .toBoolean()

            _switchSpecialCharacter.value = (DataPref.readDataStore(specialCharacters, dataStore) ?: true)
                .toString()
                .toBoolean()

            _switchUpperCase.value = (DataPref.readDataStore(uppercase, dataStore) ?: true)
                .toString()
                .toBoolean()
        }
    }

    val token: StateFlow<String>
        get() = _token

    val length: StateFlow<Float>
        get() = _length

    val allCharacters: LiveData<List<SettingsItem>>
        get() = _allCharacters

    val shouldShowEasterEggToast: StateFlow<Boolean>
        get() = _shouldShowEasterEggToast

    fun setToken(text: String) {
        _token.value = text
    }

    fun setLength(value: Float) {
        _length.value = value
    }

    var tokenNameEditText = View.GONE

    val tokenNameEditTextFieldVisibility: LiveData<Int>
        get() = _tokenNameEditTextFieldVisibility

    fun setTokenNameEditTextFieldVisibility() =
        _tokenNameEditTextFieldVisibility.postValue(View.VISIBLE)

    val tokenNameEditTextLabelVisibility: LiveData<Int>
        get() = _tokenNameEditTextLabelVisibility

    fun setTokenNameEditTextLabelVisible() =
        _tokenNameEditTextLabelVisibility.postValue(View.VISIBLE)

    fun showEasterEggToast() {
        _shouldShowEasterEggToast.value = true
    }

    fun setShouldShowToastToFalse() {
        _shouldShowEasterEggToast.value = false
    }

    fun saveSwitchState(type: Type, checked: Boolean) {
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
