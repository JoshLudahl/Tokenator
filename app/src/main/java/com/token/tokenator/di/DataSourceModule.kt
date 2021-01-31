package com.token.tokenator.di


import android.app.Application
import android.content.Context
import androidx.room.Room
import com.token.tokenator.database.TokenDao
import com.token.tokenator.database.TokenDatabase
import com.token.tokenator.database.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun providesTokenDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        TokenDatabase::class.java,
        "token_database"
    ).build()

    @Singleton
    @Provides
    fun providesTokenDao(database: TokenDatabase) = database.tokenDao()

    @Singleton
    @Provides
    fun providesRepository(tokenDao: TokenDao) = TokenRepository(tokenDao)
}
