package com.token.tokenator.utilities

import com.token.tokenator.database.settingsitem.SupportedCharacters
import com.token.tokenator.model.Type
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TokenatorTest {
    @Test
    fun testSupportedCharactersContainsAllCategories() {
        val all = SupportedCharacters.all
        assertTrue(all.isNotEmpty())

        val uppercase = all.filter { it.category == Type.UPPERCASE }
        val lowercase = all.filter { it.category == Type.LOWERCASE }
        val numeric = all.filter { it.category == Type.NUMERIC }
        val special = all.filter { it.category == Type.SPECIAL }

        assertEquals(26, uppercase.size)
        assertEquals(26, lowercase.size)
        assertEquals(10, numeric.size)
        assertTrue(special.isNotEmpty())
    }

    @Test
    fun testTokenatorExcludesCharactersCorrectly() {
        val excludedList = listOf("A", "B", "C", "1", "2", "!")
        val typesList = mutableListOf(Type.UPPERCASE, Type.LOWERCASE, Type.NUMERIC, Type.SPECIAL)

        val result =
            Tokenator.generate(
                length = 50,
                includesTypesList = typesList,
                excludedCharacters = excludedList,
                doNotRepeat = false,
            )

        for (excludedChar in excludedList) {
            assertFalse(
                "Generated password should not contain excluded character '$excludedChar'",
                result.contains(excludedChar),
            )
        }
    }
}
