package com.zeoflow.memo.compiler.processing

import com.squareup.javapoet.TypeName

/**
 * It is common for processors to check certain types against known types.
 * e.g. you may want to check if an [XType] is a [List], or an [Iterable] or is assignable from
 * an [Iterable].
 *
 * Kotlin does not model raw types, which makes it harder for our java compatibility.
 * Instead, we model them as [XRawType], a special purpose class.
 *
 * Similar to how [XMethodType] is not an [XType], [XRawType] is not an [XType] either. It has a
 * very specific use case to check against raw types and nothing else.
 *
 * Instances of XRawType implement equality.
 */
interface XRawType {
    val typeName: TypeName
    /**
     * Returns `true` if this raw type can be assigned from [other].
     */
    fun isAssignableFrom(other: XRawType): Boolean
    /**
     * Returns `true` if this raw type can be assigned from [other].
     */
    fun isAssignableFrom(other: XType) = isAssignableFrom(other.rawType)
}