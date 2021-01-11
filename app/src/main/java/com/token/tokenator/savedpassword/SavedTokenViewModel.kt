package com.token.tokenator.savedpassword

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import kotlinx.coroutines.launch

class SavedTokenViewModel @ViewModelInject constructor(private val repository: TokenRepository) : ViewModel(), LifecycleObserver {

     val tokens: LiveData<List<Token>> = repository.allTokens

     suspend fun delete(id: Int) {
         viewModelScope.launch {
         val token = repository.getOneTokenById(id)
         token?.let {
             repository.delete(it)
         }

     }
     }
}
