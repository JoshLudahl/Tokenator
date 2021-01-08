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
        includes: MutableList<Type>
    ): String {

        val sb = StringBuilder()

        if (length in 1..10000) {
            for (int in 1..length) {
                includes.shuffle()
                if (includes.size != 0) {
                    when (includes[0]) {
                        Type.LOWERCASE -> sb.append(generateRandomLowercaseLetter())
                        Type.NUMERIC -> sb.append(generateRandomNumber())
                        Type.UPPERCASE -> sb.append(generateRandomUppercaseLetter())
                        Type.SPECIAL -> sb.append(generateRandomSpecialCharacter())
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
