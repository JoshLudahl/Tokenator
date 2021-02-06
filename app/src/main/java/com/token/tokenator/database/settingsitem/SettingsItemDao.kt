package com.token.tokenator.database.settingsitem

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type

@Dao
interface SettingsItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settingsItem: SettingsItem)

    @Query("SELECT * FROM character_table")
    fun getAllCharacters(): LiveData<List<SettingsItem>>

    @Query("SELECT * FROM character_table WHERE category = :category")
    fun getAllByCategory(category: Type): LiveData<List<SettingsItem>>

    @Update
    suspend fun updateCharacter(settingsItem: SettingsItem)

}