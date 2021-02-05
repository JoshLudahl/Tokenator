package com.token.tokenator.database.settingsitem

import androidx.room.TypeConverter
import com.token.tokenator.model.Type

class SettingsTypeConverters {

    @TypeConverter
    fun stringToItemType(string: String): Type {
        return Type.valueOf(string)
    }

    @TypeConverter
    fun fromItemToString(type: Type): String {
        return type.name
    }
}