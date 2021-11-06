package com.token.tokenator.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.token.tokenator.database.settingsitem.SettingsItemDao
import com.token.tokenator.database.settingsitem.SettingsItemDatabase
import com.token.tokenator.database.settingsitem.SettingsItemRepository
import com.token.tokenator.database.token.TokenDao
import com.token.tokenator.database.token.TokenDatabase
import com.token.tokenator.database.token.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun providesTokenDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        TokenDatabase::class.java,
        "token_database"
    ).addMigrations(
        object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `passphrase` (`id` INTEGER NOT NULL, `phrase` TEXT, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }
    ).addMigrations(
        object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE token_table ADD COLUMN login TEXT DEFAULT NULL"
                )
            }
        }
    ).build()

    @Singleton
    @Provides
    fun providesTokenDao(database: TokenDatabase) =
        database.tokenDao()

    @Singleton
    @Provides
    fun providesTokenRepository(tokenDao: TokenDao) =
        TokenRepository(tokenDao)

    @Singleton
    @Provides
    fun providesSettingsItemDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        SettingsItemDatabase::class.java,
        "settingsitem_database"
    ).build()

    @Singleton
    @Provides
    fun providesSettingsItemDao(database: SettingsItemDatabase) =
        database.settingsItemDao()

    @Singleton
    @Provides
    fun providesSettingsItemRepository(settingsItemDao: SettingsItemDao) =
        SettingsItemRepository(settingsItemDao)
}
