package com.zeoflow.memo.common

/**
 * Marks a class as a field's getter or putter function.
 */
@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
public annotation class MemoFunction(
    /**
     * Set preference getter or putter function to key in the SharedPreference.
     *
     * @return The Preference key name of the SharedPreference.
     */
    val value: String
)