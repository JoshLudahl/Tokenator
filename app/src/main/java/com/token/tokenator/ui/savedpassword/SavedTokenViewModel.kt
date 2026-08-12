package com.token.tokenator.ui.savedpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.model.Token
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedTokenViewModel
    @Inject
    constructor(
        private val repository: TokenRepository,
    ) : ViewModel() {
        val tokens: StateFlow<List<Token>> =
            repository.allTokensFlow
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val noTokens: StateFlow<Boolean> =
            tokens
                .map { it.isEmpty() }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

        fun delete(token: Token) {
            viewModelScope.launch {
                repository.delete(token)
            }
        }
    }
