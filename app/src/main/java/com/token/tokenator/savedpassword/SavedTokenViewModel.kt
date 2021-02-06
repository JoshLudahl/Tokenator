package com.token.tokenator.savedpassword

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.model.Token
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedTokenViewModel @Inject constructor(private val repository: TokenRepository) :
    ViewModel(), LifecycleObserver {

    val tokens: LiveData<List<Token>> = repository.allTokens
    private val _noTokens = MutableLiveData<Boolean>()

    val noTokens: LiveData<Boolean>
        get() = _noTokens

    init {
        _noTokens.value = true
    }

    fun setTokenListEmpty(boolean: Boolean) {
        _noTokens.value = boolean
    }

    suspend fun delete(token: Token) {
            repository.delete(token)
    }
}
