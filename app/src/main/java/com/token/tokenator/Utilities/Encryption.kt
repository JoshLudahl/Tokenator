package com.token.tokenator.Utilities

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object Encryption {

    // change to plaintext then pull bytearray out for simplicity

    @Throws(Exception::class)
    fun encrypt(plaintext: ByteArray?, key: SecretKey, IV: ByteArray?): ByteArray? {
        val cipher = Cipher.getInstance("AES")
        val keySpec = SecretKeySpec(key.encoded, "AES")
        val ivSpec = IvParameterSpec(IV)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(plaintext)
    }

    fun decrypt(cipherText: ByteArray?, key: SecretKey, IV: ByteArray?): String? {
        try {
            val cipher = Cipher.getInstance("AES")
            val keySpec = SecretKeySpec(key.encoded, "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decryptedText = cipher.doFinal(cipherText)
            return String(decryptedText)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}