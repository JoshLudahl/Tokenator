package com.token.tokenator.database.settingsitem

import androidx.room.TypeConverter
import com.token.tokenator.model.Type

class SettingsItemTypeConverters {
    @TypeConverter
    fun toType(string: String) = enumValueOf<Type>(string)

    @TypeConverter
    fun fromType(type: Type?) = type?.name
}
