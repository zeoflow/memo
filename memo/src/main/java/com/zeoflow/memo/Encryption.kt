package com.zeoflow.memo

/**
 * Intermediate layer which is used by Memo to encrypt and decrypt the given values.
 *
 *
 * Use custom implementation if built-in implementation is not enough.
 *
 * @see ConcealEncryption
 */
interface Encryption {
    fun encryptionKey(): String?

    /**
     * Initialize the encryption algorithm, If the device does not support required
     * crypto return false
     *
     * @return true if crypto is supported
     */
    fun init(): Boolean

    /**
     * Encrypt the given string and returns cipher text
     *
     * @param key   is the given key
     * @param value is the plain text
     *
     * @return cipher text as string
     */
    @Throws(Exception::class)
    fun encrypt(key: String?, value: String): String?

    /**
     * Decrypt the given cipher text and return plain text
     *
     * @param key   is the given key
     * @param value is the cipher text
     *
     * @return plain text
     */
    @Throws(Exception::class)
    fun decrypt(key: String?, value: String?): String
}