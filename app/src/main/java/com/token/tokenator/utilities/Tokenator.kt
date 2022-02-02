package com.token.tokenator.utilities

import android.util.Log
import com.token.tokenator.model.Type
import kotlin.random.Random
import kotlin.random.nextInt

object Tokenator {

    private val arrayOfSpecialCharacters =
        arrayListOf(
            33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 45, 46, 47, 58, 59, 60, 61, 62,
            63, 64, 91, 92, 93, 94, 95, 96, 123, 124, 125, 126
        )

    private const val TOTAL_CHARACTERS = 92

    /**
     * Simple password generator.
     *
     * @param length takes the length the password should be
     * @param includesTypesList takes a list of character types to include
     */
    fun generate(
        length: Int,
        includesTypesList: MutableList<Type>,
        excludedCharacters: List<String>,
        doNotRepeat: Boolean,
        includePhrase: String = ""
    ): String {

        val sb = StringBuilder()

        var isValidated = false
        while (!isValidated) {
            Log.d("Token:", "Clearing current token")
            sb.clear()

            Log.d("Token:", "Adding phrase")
            sb.append(includePhrase)
            if (includePhrase.length >= length) return includePhrase
            Log.d("Token:", "Generating new token.")
            val token = generateTokenString(
                excludedCharacters = excludedCharacters,
                length = length - includePhrase.length,
                passphrase = includePhrase,
                shouldNotRepeat = doNotRepeat,
                typesList = includesTypesList
            )
            sb.append(token)

            Log.d("Token:", "Checking if need to validate new token")

            val phraseLengthAndLength = includePhrase.length + length
            Log.d("Token: phrase and length", "${phraseLengthAndLength}")


            val totalMinusExcludedSize = TOTAL_CHARACTERS - excludedCharacters.size
            Log.d("Token: totesMinus", "$totalMinusExcludedSize")


            val phraseLengthAndTypeListSize = includePhrase.length + includesTypesList.size
            Log.d("Token: ippitl", "$phraseLengthAndTypeListSize")

            isValidated = if (
                phraseLengthAndLength <= totalMinusExcludedSize
                && phraseLengthAndTypeListSize > length
                && includePhrase.length + 1 != length
            ) {
                Log.d("Token: validating", token)
                isValidated(
                    token = sb,
                    typeList = includesTypesList
                )
            } else {
                true
            }
        }
        return sb.toString()
    }

    private fun generateTokenString(
        excludedCharacters: List<String>,
        length: Int,
        passphrase: String,
        shouldNotRepeat: Boolean,
        typesList: MutableList<Type>
    ): String {
        val token = StringBuilder()
        var looper = 0

        while (token.length < length && looper < 15000) {
            if (typesList.isEmpty()) {
                token.append(generateRandomLowercaseLetter())
            } else {
                val character = shuffleAndGetChar(typesList)
                if (excludedCharacters.contains(character).not()) {
                    if (shouldNotRepeat) {
                        if (!token.contains(character) && !passphrase.contains(character)) {
                            token.append(character)
                        }
                    } else {
                        token.append(character)
                    }
                }
            }
            Log.i("Token: ", "$token")
            looper++
        }
        return token.toString()
    }

    private fun isValidated(
        token: StringBuilder,
        typeList: MutableList<Type>
    ): Boolean {
        if (containsAllSpecialTypes(string = token.toString(), typeList)) return true
        return false
    }

    private fun shuffleAndGetChar(typeList: MutableList<Type>): String {
        typeList.shuffle()
        return when (typeList[0]) {
            Type.LOWERCASE -> generateRandomLowercaseLetter()
            Type.NUMERIC -> generateRandomNumber()
            Type.UPPERCASE -> generateRandomUppercaseLetter()
            Type.SPECIAL -> generateRandomSpecialCharacter()
        }.toString()
    }

    private fun containsAllSpecialTypes(string: String, typelist: List<Type>): Boolean {
        typelist.forEach { type ->
            when (type) {
                Type.LOWERCASE -> (97..122).map { it.toChar().toString() }.toList()
                Type.NUMERIC -> (0..9).map { it.toString() }.toList()
                Type.SPECIAL -> arrayOfSpecialCharacters.map { it.toChar().toString() }
                Type.UPPERCASE -> (65..90).map { it.toChar().toString() }.toList()
            }.let { list ->
                if (!stringContainsItem(string, list.map { it })) return false
            }
        }
        return true
    }

    private fun stringContainsItem(string: String, charList: List<String>): Boolean {
        charList.forEach { numericValue ->
            if (string.contains(numericValue)) return true
        }
        return false
    }

    private fun getRandomNumber(lowerBound: Int, upperBound: Int): Int {
        return Random.nextInt(lowerBound..upperBound)
    }

    private fun generateRandomNumber(): Int {
        return getRandomNumber(0, 9)
    }

    private fun generateRandomLowercaseLetter(): Char {
        return getRandomNumber(97, 122).toChar()
    }

    private fun generateRandomUppercaseLetter(): Char {
        return getRandomNumber(65, 90).toChar()
    }

    private fun generateRandomSpecialCharacter(): Char {
        val index = getRandomNumber(0, arrayOfSpecialCharacters.size - 1)
        return arrayOfSpecialCharacters[index].toChar()
    }
}
