package com.token.tokenator.utilities

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class EncryptionTest {

    @Test
    fun testEncryptionDecryptionV2() {
        val originalText = "SensitivePassword123"
        val encryptedText = Encryption.encrypt(originalText)
        assertNotNull("Encryption failed", encryptedText)
        assertTrue("Encrypted text should start with v2:", encryptedText!!.startsWith("v2:"))

        val decryptedText = Encryption.decrypt(encryptedText)
        assertEquals("Decryption result does not match original", originalText, decryptedText)
    }

    @Test
    fun testEmptyString() {
        val originalText = ""
        val encryptedText = Encryption.encrypt(originalText)
        assertEquals("", encryptedText)

        val decryptedText = Encryption.decrypt("")
        assertEquals("", decryptedText)
    }

    @Test
    fun testLegacyDecryptionFallback() {
        // This is a dummy test to ensure the logic doesn't crash.
        // In a real scenario, we'd use a known legacy encrypted string.
        val legacyInput = "SomeLegacyEncryptedStringThatWillFailButShouldNotCrash"
        try {
            Encryption.decrypt(legacyInput)
            // It will likely return null or fail because the input isn't valid Base64 or doesn't match keys,
            // but we want to ensure it routes to legacyDecrypt.
        } catch (e: Exception) {
            fail("Legacy fallback should handle exceptions internally and not crash.")
        }
    }
}
