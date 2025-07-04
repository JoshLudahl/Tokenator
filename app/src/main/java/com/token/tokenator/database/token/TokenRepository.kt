package com.token.tokenator.database.token

import androidx.lifecycle.LiveData
import com.token.tokenator.model.Passphrase
import com.token.tokenator.model.Token
import kotlinx.coroutines.flow.Flow

class TokenRepository(private val tokenDao: TokenDao) {
    val allTokens: LiveData<List<Token>> = tokenDao.getAllTokens()
    val allTokensByName: LiveData<List<Token>> = tokenDao.getAllTokensByName()
    val allTokensByDate: LiveData<List<Token>> = tokenDao.getAllTokensByDate()
    val passphrase: LiveData<Passphrase>? = tokenDao.getPassphrase()
    val passphraseflow: Flow<Passphrase?> = tokenDao.getPassphraseFlow()

    suspend fun insert(token: Token) = tokenDao.insert(token)

    suspend fun insertPassphrase(passphrase: Passphrase) = tokenDao.insertPhrase(passphrase)

    suspend fun delete(token: Token) = tokenDao.delete(token)

    fun updateToken(token: Token) = tokenDao.updateToken(token)

    suspend fun getOneTokenById(id: Int): Token? = tokenDao.getTokenById(id)

    fun getOneTokenByName(name: String): Token? = tokenDao.getTokenByName(name)
}
