package com.zeoflow.memo

/**
 * Intermediate layer that is used to serialize/deserialize the cipher text
 *
 *
 * Use custom implementation if built-in implementation is not enough.
 *
 * @see MemoSerializer
 */
interface ISerializer {
    /**
     * Serialize the cipher text along with the given data type
     *
     * @return serialized string
     */
    fun <T> serialize(cipherText: String, value: T): String

    /**
     * Deserialize the given text according to given DataInfo
     *
     * @return original object
     */
    fun deserialize(plainText: String): DataInfo
}