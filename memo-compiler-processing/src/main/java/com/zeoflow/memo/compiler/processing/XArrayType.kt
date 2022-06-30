package com.zeoflow.memo.compiler.processing

/**
 * Represents an Array type including Kotlin's [Array] type.
 *
 * @see [javax.lang.model.type.ArrayType]
 */
interface XArrayType : XType {
    /**
     * The type of elements in the Array
     */
    val componentType: XType
}