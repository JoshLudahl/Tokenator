package com.token.tokenator.di

import android.app.Application
import com.token.tokenator.database.TokenDatabase
import com.token.tokenator.database.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun providesRepository(application: Application): TokenRepository {
        val tokenDao = TokenDatabase.getDatabase(application).tokenDao()
        return TokenRepository(tokenDao)
    }
}
