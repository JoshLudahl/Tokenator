package com.token.tokenator.database.settingsitem

import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type

object SupportedCharacters {
    private val arrayOfSpecialCharacters =
        arrayListOf(
            33,
            34,
            35,
            36,
            37,
            38,
            39,
            40,
            41,
            42,
            43,
            44,
            45,
            46,
            47,
            58,
            59,
            60,
            61,
            62,
            63,
            64,
            91,
            92,
            93,
            94,
            95,
            96,
            123,
            124,
            125,
            126,
        )

    val all: List<SettingsItem> by lazy {
        val list = mutableListOf<SettingsItem>()

        // Uppercase
        for (ch in 'A'..'Z') {
            list.add(SettingsItem(item = ch.toString(), included = true, category = Type.UPPERCASE))
        }

        // Lowercase
        for (ch in 'a'..'z') {
            list.add(SettingsItem(item = ch.toString(), included = true, category = Type.LOWERCASE))
        }

        // Numeric
        for (ch in '0'..'9') {
            list.add(SettingsItem(item = ch.toString(), included = true, category = Type.NUMERIC))
        }

        // Special
        for (code in arrayOfSpecialCharacters) {
            list.add(SettingsItem(item = code.toChar().toString(), included = true, category = Type.SPECIAL))
        }

        list
    }
}
