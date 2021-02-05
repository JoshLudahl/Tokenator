package com.token.tokenator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_table")
data class SettingsItem(

    @ColumnInfo(name = "item")
    val item: String,

    @ColumnInfo(name = "included")
    var included: Boolean,

    @ColumnInfo(name = "category")
    val category: Type
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
