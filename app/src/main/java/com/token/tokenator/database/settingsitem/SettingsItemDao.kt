package com.token.tokenator.database.settingsitem

import androidx.lifecycle.LiveData
import androidx.room.*
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settingsItem: SettingsItem)

    @Query("SELECT * FROM character_table")
    fun getAllCharacters(): Flow<List<SettingsItem>>

    @Query("SELECT * FROM character_table WHERE category = :category")
    fun getAllByCategory(category: Type): LiveData<List<SettingsItem>>

    @Query("SELECT * FROM character_table WHERE included is 1")
    fun getAllExcluded(): LiveData<List<SettingsItem>>

    @Update
    suspend fun updateCharacter(settingsItem: SettingsItem)
}
