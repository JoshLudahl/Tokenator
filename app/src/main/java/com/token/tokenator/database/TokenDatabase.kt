package com.token.tokenator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.token.tokenator.model.Token

@Database(entities = [Token::class], version = 1, exportSchema = false)
abstract class TokenDatabase : RoomDatabase() {

    abstract fun poopDao(): TokenDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TokenDatabase? = null

        fun getDatabase(context: Context): TokenDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TokenDatabase::class.java,
                    "token_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
