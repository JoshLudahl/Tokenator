package com.token.tokenator.ui.savedpassword.passworddetails

import androidx.lifecycle.ViewModel
import com.token.tokenator.database.token.TokenRepository
import com.token.tokenator.model.Token
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordDetailViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {

}