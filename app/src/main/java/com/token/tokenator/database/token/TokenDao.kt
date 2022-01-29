package com.token.tokenator.database.token

import androidx.lifecycle.LiveData
import androidx.room.*
import com.token.tokenator.model.Passphrase
import com.token.tokenator.model.Token
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrase(passphrase: Passphrase)

    @Query("SELECT * FROM passphrase WHERE id = 0")
    fun getPassphrase(): LiveData<Passphrase>?

    @Query("SELECT * FROM passphrase WHERE id = 0")
    fun getPassphraseFlow(): Flow<Passphrase>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: Token)

    @Delete
    suspend fun delete(token: Token)

    @Query("SELECT * FROM token_table")
    fun getAllTokens(): LiveData<List<Token>>

    @Query("SELECT * FROM token_table ORDER BY title ASC")
    fun getAllTokensByName(): LiveData<List<Token>>

    @Query("SELECT * FROM token_table ORDER BY date_saved ASC")
    fun getAllTokensByDate(): LiveData<List<Token>>

    @Query("SELECT * FROM token_table WHERE id = :id")
    suspend fun getTokenById(id: Int): Token?

    @Query("SELECT * FROM token_table WHERE title = :title")
    suspend fun getTokenByName(title: String): Token?

    @Update
    suspend fun updateToken(token: Token)
}
