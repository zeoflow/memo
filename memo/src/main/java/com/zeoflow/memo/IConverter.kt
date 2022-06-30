package com.zeoflow.memo

/**
 * Used to handle encoding and decoding as an intermediate layer.
 *
 * Implement this interface if a custom implementation is needed
 *
 * @see MemoConverter
 */
interface IConverter {
    /**
     * Encodes the value
     *
     * @param value will be encoded
     *
     * @return the encoded string
     */
    fun <T> toString(value: T?): String?

    /**
     * Decodes
     *
     * @param value is the encoded data
     *
     * @return the plain value
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    fun <T> fromString(value: String?, dataInfo: DataInfo): T?
}