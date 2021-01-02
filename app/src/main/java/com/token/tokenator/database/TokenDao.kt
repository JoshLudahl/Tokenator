package com.token.tokenator.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.token.tokenator.model.Token

@Dao
interface TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: Token)

    @Query("SELECT * FROM token_table")
    fun getAllTokens(): LiveData<List<Token>>

    @Query("SELECT * FROM token_table ORDER BY title")
    fun getAllTokensByName(): LiveData<List<Token>>

    @Query("SELECT * FROM token_table ORDER BY date_saved")
    fun getAllTokensByDate(): LiveData<List<Token>>
}
