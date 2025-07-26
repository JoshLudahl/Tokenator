package com.token.tokenator.di

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object Utilities {
    @Singleton
    @Provides
    fun providesClipboardManager(
        @ApplicationContext context: Context,
    ): ClipboardManager =
        ContextCompat.getSystemService(
            context,
            ClipboardManager::class.java,
        ) as ClipboardManager

    @Singleton
    @Provides
    fun providesDataStorePreferences(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @DataStoreCharacterPopulation
    @Singleton
    @Provides
    fun providesDataStoreCharacterPopulation(): String = "character_populated"

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

    @DataStorePassPhrase
    @Singleton
    @Provides
    fun dataStorePassPhraseString(): String = "PASSPHRASE"

    @DataStorePassPhraseIncluded
    @Singleton
    @Provides
    fun dataStorePassPhraseIncludedString(): String = "PASSPHRASE_INCLUDED"
}
