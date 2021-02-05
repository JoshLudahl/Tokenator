package com.token.tokenator.database.settingsitem

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.token.tokenator.model.SettingsItem

@Database(
    entities = [SettingsItem::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(SettingsTypeConverters::class)
abstract class SettingsItemDatabase : RoomDatabase() {

    abstract fun settingsItemDao(): SettingsItemDao

}
