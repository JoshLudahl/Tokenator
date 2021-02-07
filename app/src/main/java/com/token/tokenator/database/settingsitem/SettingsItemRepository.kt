package com.token.tokenator.database.settingsitem

import android.provider.Settings
import androidx.lifecycle.LiveData
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type

class SettingsItemRepository(private val settingsItemDao: SettingsItemDao) {

    val allCharacters: LiveData<List<SettingsItem>> = settingsItemDao.getAllCharacters()

    val allSpecialChars: LiveData<List<SettingsItem>> = settingsItemDao.getAllByCategory(Type.SPECIAL)
    val allNumericChars: LiveData<List<SettingsItem>> = settingsItemDao.getAllByCategory(Type.NUMERIC)
    val allLowerCaseChars: LiveData<List<SettingsItem>> = settingsItemDao.getAllByCategory(Type.LOWERCASE)
    val allUpperCaseChars: LiveData<List<SettingsItem>> = settingsItemDao.getAllByCategory(Type.UPPERCASE)

    suspend fun insert(settingsItem: SettingsItem) {
        settingsItemDao.insert(settingsItem)
    }

    suspend fun update(settingsItem: SettingsItem) {
        settingsItemDao.updateCharacter(settingsItem)
    }
}
