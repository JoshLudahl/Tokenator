package com.token.tokenator.database.token

import androidx.room.Database
import androidx.room.RoomDatabase
import com.token.tokenator.model.Token

@Database(
    entities = [Token::class],
    version = 4,
    exportSchema = false
)
abstract class TokenDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

}
