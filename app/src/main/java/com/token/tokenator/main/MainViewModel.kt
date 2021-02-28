package com.token.tokenator.main

import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.*
import com.token.tokenator.BuildConfig
import com.token.tokenator.Utilities.Encryption
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.di.DataStoreNoRepeat
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Token
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var repository: TokenRepository,
    settingsItemRepository: SettingsItemRepository,
    dataStore: DataStore<Preferences>,
    @DataStoreNoRepeat private var noRepeat: String
) :
    ViewModel(), LifecycleObserver {

    var version: String
    private val _token = MutableStateFlow<String>("")
    private val _length = MutableStateFlow<Float>(0f)
    private val _tokenNameEditTextLabelVisibility = MutableLiveData<Int>()
    private val _tokenNameEditTextFieldVisibility = MutableLiveData<Int>()
    private val _allCharacters = settingsItemRepository.allCharacters
    private val _shouldShowEasterEggToast = MutableStateFlow<Boolean>(false)

    val noRepeatFlow: Flow<Boolean> = dataStore.data.map {
        preferences -> (preferences[stringPreferencesKey(noRepeat)]).toBoolean()
    }

    init {
        Log.i("MainViewModel", "Initialized")
        version = "Version ${BuildConfig.VERSION_NAME}"
        Log.i("VERSION", version)
        _tokenNameEditTextLabelVisibility.value = View.GONE
        _tokenNameEditTextFieldVisibility.value = View.GONE
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
