package com.token.tokenator.di

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
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

    @Singleton
    @Provides
    fun providesDataStorePreferences(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.createDataStore(name = "settings")
    }

    @DataStoreFeature
    @Singleton
    @Provides
    fun providesDataStoreFeatureString(): String = "feature_discovery"

    @DataStoreLowercase
    @Singleton
    @Provides
    fun dataStoreLowercaseString(): String = "LOWERCASE"

    @DataStoreNoRepeat
    @Singleton
    @Provides
    fun dataStoreNoRepeatString(): String = "NO_REPEAT"

    @DataStoreNumeric
    @Singleton
    @Provides
    fun dataStoreNumericString(): String = "NUMERIC"

    @DataStoreSpecialCharacters
    @Singleton
    @Provides
    fun dataStoreSpecialCharactersString(): String = "SPECIAL_CHARACTERS"

    @DataStoreUppercase
    @Singleton
    @Provides
    fun dataStoreUppercaseString(): String = "UPPERCASE"
}
