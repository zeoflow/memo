package com.zeoflow.memo.common

/**
 * Marks a field as an SharedPreference key. This field will be mapped as the SharedPreference key
 * with Upper camel case.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
public annotation class Observable 