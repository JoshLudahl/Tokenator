package com.token.tokenator.savedpassword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.token.tokenator.database.TokenDatabase
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token

class SavedTokenViewModel(application: Application) : AndroidViewModel(application) {

     val tokens: LiveData<List<Token>>

    private val repository: TokenRepository

    init {
        val tokenDao = TokenDatabase.getDatabase(application).tokenDao()
        repository = TokenRepository(tokenDao)
        tokens = repository.allTokens
    }

}