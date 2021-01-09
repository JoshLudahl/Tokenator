package com.token.tokenator.savedpassword

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token

class SavedTokenViewModel @ViewModelInject constructor(repository: TokenRepository) : ViewModel(), LifecycleObserver {

     val tokens: LiveData<List<Token>> = repository.allTokens
}
