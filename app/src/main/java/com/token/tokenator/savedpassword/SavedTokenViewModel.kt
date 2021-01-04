package com.token.tokenator.savedpassword

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.token.tokenator.database.TokenDatabase
import com.token.tokenator.database.TokenRepository
import com.token.tokenator.model.Token
import javax.inject.Named

class SavedTokenViewModel @ViewModelInject constructor(repository: TokenRepository) : ViewModel(), LifecycleObserver {

     val tokens: LiveData<List<Token>>

    init {
        tokens = repository.allTokens

    }



}