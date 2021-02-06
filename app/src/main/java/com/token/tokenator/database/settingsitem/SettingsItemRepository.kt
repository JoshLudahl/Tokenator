package com.token.tokenator.database.settingsitem

import androidx.lifecycle.LiveData
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type

class SettingsItemRepository(private val settingsItemDao: SettingsItemDao) {

    val allSpecialChars: LiveData<List<SettingsItem>> = settingsItemDao.getAllByCategory(Type.SPECIAL)

    suspend fun insert(settingsItem: SettingsItem) {
        settingsItemDao.insert(settingsItem)
    }
}
