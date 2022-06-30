package com.zeoflow.memo.common

/**
 * Marks a field as an SharedPreference key. This field will be mapped as the SharedPreference key
 * with Upper camel case.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
public annotation class KeyName(
    /**
     * Preference Key name value in the SharedPreference. If not used, defaults to the field value
     * with Upper camel case.
     *
     * @return The preference key name value of the SharedPreference.
     */
    public val value: String
)