package com.token.tokenator.database.settingsitem

import androidx.lifecycle.LiveData
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type
import kotlinx.coroutines.flow.Flow

class SettingsItemRepository(private val settingsItemDao: SettingsItemDao) {

    val allCharacters: Flow<List<SettingsItem>> =
        settingsItemDao.getAllCharacters()

    val allExcludedCharacters: LiveData<List<SettingsItem>> =
        settingsItemDao.getAllExcluded()

    val allSpecialChars: LiveData<List<SettingsItem>> =
        settingsItemDao.getAllByCategory(Type.SPECIAL)

    val allNumericChars: LiveData<List<SettingsItem>> =
        settingsItemDao.getAllByCategory(Type.NUMERIC)

    val allLowerCaseChars: LiveData<List<SettingsItem>> =
        settingsItemDao.getAllByCategory(Type.LOWERCASE)

    val allUpperCaseChars: LiveData<List<SettingsItem>> =
        settingsItemDao.getAllByCategory(Type.UPPERCASE)

    suspend fun insert(settingsItem: SettingsItem) =
        settingsItemDao.insert(settingsItem)

    suspend fun update(settingsItem: SettingsItem) =
        settingsItemDao.updateCharacter(settingsItem)
}
