package com.token.tokenator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_table",
    indices = [Index(value = arrayOf("item"), unique = true)]
)
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
