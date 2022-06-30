package com.zeoflow.memo

import android.util.Base64

/**
 * Provides Base64 encoding as non-encryption option.
 * This doesn't provide any encryption
 */
class NoEncryption : Encryption {
    override fun encryptionKey(): String? {
        return null
    }

    override fun init(): Boolean {
        return true
    }

    @Throws(Exception::class)
    override fun encrypt(key: String?, value: String): String? {
        return encodeBase64(value.toByteArray())
    }

    @Throws(Exception::class)
    override fun decrypt(key: String?, value: String?): String {
        return String(decodeBase64(value))
    }

    fun encodeBase64(bytes: ByteArray?): String {
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decodeBase64(value: String?): ByteArray {
        return Base64.decode(value, Base64.DEFAULT)
    }
}