package com.token.tokenator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.token.tokenator.model.Token

@Database(entities = [Token::class], version = 3, exportSchema = false)
abstract class TokenDatabase : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

    companion object {

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
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
