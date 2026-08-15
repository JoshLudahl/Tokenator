package com.token.tokenator.database.settingsitem

import androidx.lifecycle.LiveData
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsItemRepository(
    private val settingsItemDao: SettingsItemDao,
) {
    val inactiveCharacters: Flow<List<SettingsItem>> =
        settingsItemDao.getInactiveCharacters()

    val allCharacters: Flow<List<SettingsItem>> =
        inactiveCharacters.map { inactiveList ->
            val inactiveItemsSet = inactiveList.map { it.item }.toSet()
            SupportedCharacters.all.map { supportedItem ->
                supportedItem.copy(included = !inactiveItemsSet.contains(supportedItem.item))
            }
        }

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

    suspend fun insert(settingsItem: SettingsItem) = settingsItemDao.insert(settingsItem)

    suspend fun update(settingsItem: SettingsItem) {
        if (settingsItem.included) {
            // Now included (active), remove from inactive table
            settingsItemDao.deleteByItem(settingsItem.item)
        } else {
            // Now excluded (inactive), store in inactive table
            settingsItemDao.insert(settingsItem.copy(included = false))
        }
    }

    suspend fun deleteAllCharacters() = settingsItemDao.deleteAllCharacters()
}
