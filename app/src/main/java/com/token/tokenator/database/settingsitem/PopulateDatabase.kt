package com.token.tokenator.database.settingsitem

import com.token.tokenator.model.SettingsItem
import com.token.tokenator.model.Type

object PopulateDatabase {
    private val listOfLowerCaseLetters = mutableListOf<Char>()
    private val listOfUpperCaseLetters = mutableListOf<Char>()
    private val listOfNumeric = mutableListOf<Char>()
    private val listOfSpecialCharacters = mutableListOf<Char>()

    private fun populateList(
        list: MutableList<Char>,
        lowerBound: Int,
        upperBound: Int,
    ) {
        for (index in lowerBound until upperBound.inc()) {
            list.add(index.toChar())
        }
    }

    private fun populateLists() {
        populateList(
            list = listOfLowerCaseLetters,
            lowerBound = 97,
            upperBound = 122,
        )

        populateList(
            list = listOfUpperCaseLetters,
            lowerBound = 65,
            upperBound = 90,
        )

        populateList(
            list = listOfNumeric,
            lowerBound = 48,
            upperBound = 57,
        )

        // Next few are specifically for populating special characters since char values are split.
        populateList(
            list = listOfSpecialCharacters,
            lowerBound = 33,
            upperBound = 47,
        )
        populateList(
            list = listOfSpecialCharacters,
            lowerBound = 58,
            upperBound = 64,
        )
        populateList(
            list = listOfSpecialCharacters,
            lowerBound = 91,
            upperBound = 96,
        )
        populateList(
            list = listOfSpecialCharacters,
            lowerBound = 123,
            upperBound = 126,
        )
    }

    suspend fun populateDatabase(database: SettingsItemRepository) {
        populateLists()
        listOfSpecialCharacters.forEach {
            database.insert(
                buildItem(it, Type.SPECIAL),
            )
        }

        listOfNumeric.forEach {
            database.insert(
                buildItem(it, Type.NUMERIC),
            )
        }

        listOfLowerCaseLetters.forEach {
            database.insert(
                buildItem(it, Type.LOWERCASE),
            )
        }

        listOfUpperCaseLetters.forEach {
            database.insert(
                buildItem(it, Type.UPPERCASE),
            )
        }
    }

    private fun buildItem(
        number: Char,
        type: Type,
    ): SettingsItem =
        SettingsItem(
            item = number.toString(),
            included = true,
            category = type,
        )
}
