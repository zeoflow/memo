package com.zeoflow.memo.common

/**
 * Marks a class as a multiple field's getter or putter function.
 */
@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
public annotation class MemoCompoundFunction(
    /**
     * Set preference getter or putter function to key in the SharedPreference.
     *
     * @return The Preference keys name of the SharedPreference.
     */
    val values: Array<String>
)