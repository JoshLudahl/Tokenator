package com.token.tokenator.database

import androidx.lifecycle.LiveData
import com.token.tokenator.model.Token

class TokenRepository(private val tokenDao: TokenDao) {

    val allTokens: LiveData<List<Token>> = tokenDao.getAllTokens()

    fun insert(token: Token) {
        tokenDao.insert(token)
    }

}
