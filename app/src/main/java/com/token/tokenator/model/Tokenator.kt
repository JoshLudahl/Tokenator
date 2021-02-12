package com.token.tokenator.model

import kotlin.random.Random
import kotlin.random.nextInt

object Tokenator {
    /**
     * Simple password generator.
     *
     * @param length takes the length the password should be
     * @param includes takes a list of character types to include
     */
    fun generate(
        length: Int,
        includes: MutableList<Type>,
        excludedCharacters: List<String>
    ): String {

        val sb = StringBuilder()

        if (length in 1..1000) {
            while (sb.length < length) {
                includes.shuffle()

                if (includes.size != 0) {
                    val character = when (includes[0]) {
                        Type.LOWERCASE -> generateRandomLowercaseLetter().toString()
                        Type.NUMERIC -> generateRandomNumber().toString()
                        Type.UPPERCASE -> generateRandomUppercaseLetter().toString()
                        Type.SPECIAL -> generateRandomSpecialCharacter().toString()
                    }

                    if (excludedCharacters.contains(character).not()) {
                        sb.append(character)
                    }

                } else {
                    sb.append(generateRandomLowercaseLetter())
                }
            }
        }
        return sb.toString()
    }

    private fun getRandomNumber(lowerBound: Int, upperBound: Int): Int {
        return Random.nextInt(lowerBound..upperBound)
    }

    private fun generateRandomNumber(): Int {
        return getRandomNumber(1, 9)
    }

    private fun generateRandomLowercaseLetter(): Char {
        return getRandomNumber(97, 122).toChar()
    }

    private fun generateRandomUppercaseLetter(): Char {
        return getRandomNumber(65, 90).toChar()
    }

    private fun generateRandomSpecialCharacter(): Char {
        val arrayOfSpecialCharacters =
            arrayListOf(33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 45, 46, 47, 60, 61, 62, 63, 64)
        val index = getRandomNumber(0, arrayOfSpecialCharacters.size - 1)
        return arrayOfSpecialCharacters[index].toChar()
    }
}
