package com.token.tokenator.utilities

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.token.tokenator.BuildConfig
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val SECRET_KEY = BuildConfig.SECRET_KEY
private const val SALT = BuildConfig.SALT
private const val IV = BuildConfig.IV

private const val ALIAS = "tokenator_key"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val AES_MODE = "AES/GCM/NoPadding"
private const val V2_PREFIX = "v2:"

object Encryption {
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val entry = keyStore.getEntry(ALIAS, null)
        if (entry is KeyStore.SecretKeyEntry) {
            return entry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec =
            KeyGenParameterSpec
                .Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun encrypt(strToEncrypt: String): String? {
        if (strToEncrypt.isEmpty()) return ""
        try {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8))

            // Combine IV and Ciphertext: [IV_length (1 byte)][IV bytes][Ciphertext bytes]
            val combined = ByteArray(1 + iv.size + encryptedBytes.size)
            combined[0] = iv.size.toByte()
            System.arraycopy(iv, 0, combined, 1, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, 1 + iv.size, encryptedBytes.size)

            return V2_PREFIX + Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt: String): String? {
        if (strToDecrypt.isEmpty()) return ""
        if (strToDecrypt.startsWith(V2_PREFIX)) {
            return secureDecrypt(strToDecrypt.removePrefix(V2_PREFIX))
        }
        return legacyDecrypt(strToDecrypt)
    }

    private fun secureDecrypt(encryptedBase64: String): String? {
        try {
            val combined = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val ivLength = combined[0].toInt()
            val iv = ByteArray(ivLength)
            val encryptedBytes = ByteArray(combined.size - 1 - ivLength)

            System.arraycopy(combined, 1, iv, 0, ivLength)
            System.arraycopy(combined, 1 + ivLength, encryptedBytes, 0, encryptedBytes.size)

            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(AES_MODE)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            return String(cipher.doFinal(encryptedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            println("Error while secure decrypting: $e")
        }
        return null
    }

    private fun legacyDecrypt(strToDecrypt: String): String? {
        try {
            val ivParameterSpec = IvParameterSpec(Base64.decode(IV, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =
                PBEKeySpec(
                    SECRET_KEY.toCharArray(),
                    Base64.decode(SALT, Base64.DEFAULT),
                    10000,
                    256,
                )
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
            return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        } catch (e: Exception) {
            println("Error while legacy decrypting: $e. Input string length: ${strToDecrypt.length}")
        }
        return null
    }
}
