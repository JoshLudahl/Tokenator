package com.token.tokenator.database.settingsitem

import androidx.room.TypeConverter
import com.token.tokenator.model.Type

class SettingsItemTypeConverters {

    @TypeConverter
    fun stringToItemType(string: String): Type {
        return enumValueOf(string)
    }

    @TypeConverter
    fun fromItemToString(type: Type): String {
        return type.name
    }
}
