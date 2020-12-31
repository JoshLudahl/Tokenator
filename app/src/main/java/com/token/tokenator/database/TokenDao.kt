package com.token.tokenator.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.token.tokenator.model.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM token_table")
    fun getAllTokens(): LiveData<List<Token>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(token: Token)

}
