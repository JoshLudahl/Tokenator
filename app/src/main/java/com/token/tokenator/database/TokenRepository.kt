package com.token.tokenator.database

import androidx.lifecycle.LiveData
import com.token.tokenator.model.Token

class TokenRepository(private val tokenDao: TokenDao) {

    val allTokens: LiveData<List<Token>> = tokenDao.getAllTokens()
    val allTokensByName: LiveData<List<Token>> = tokenDao.getAllTokensByName()
    val allTokensByDate: LiveData<List<Token>> = tokenDao.getAllTokensByDate()

    fun insert(token: Token) {
        tokenDao.insert(token)
    }

    fun delete(token: Token) {
        tokenDao.delete(token)
    }
}
