package com.token.tokenator.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.token.tokenator.model.Token

@Dao
interface TokenDao {

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
}
