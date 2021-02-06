package com.token.tokenator.di

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Utilities {

    @Singleton
    @Provides
    fun providesClipboardManager(@ApplicationContext context: Context): ClipboardManager {
        return ContextCompat.getSystemService(
            context,
            ClipboardManager::class.java
        ) as ClipboardManager
    }
}
