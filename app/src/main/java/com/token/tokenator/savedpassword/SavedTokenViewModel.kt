package com.token.tokenator.savedpassword

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import kotlinx.coroutines.launch

class SavedTokenViewModel @ViewModelInject constructor(private val repository: TokenRepository) :
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

    suspend fun delete(id: Int) {
        viewModelScope.launch {
            val token = repository.getOneTokenById(id)
            token?.let {
                repository.delete(it)
            }
        }
    }
}
