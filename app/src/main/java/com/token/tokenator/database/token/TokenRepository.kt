package com.token.tokenator.database.token

import androidx.lifecycle.LiveData
import com.token.tokenator.model.Token

class TokenRepository(private val tokenDao: TokenDao) {

    val allTokens: LiveData<List<Token>> = tokenDao.getAllTokens()
    val allTokensByName: LiveData<List<Token>> = tokenDao.getAllTokensByName()
    val allTokensByDate: LiveData<List<Token>> = tokenDao.getAllTokensByDate()

    suspend fun insert(token: Token) {
        tokenDao.insert(token)
    }

    suspend fun delete(token: Token) {
        tokenDao.delete(token)
    }

    suspend fun getOneTokenById(id: Int): Token? {
        return tokenDao.getTokenById(id)
    }

    suspend fun getOneTokenByName(name: String): Token? = tokenDao.getTokenByName(name)
}