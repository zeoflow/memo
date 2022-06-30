package com.zeoflow.memo.common

/**
 * Marks a class as an default SharedPreference entity. This entity will be mapped the default
 * SharedPreference persistence data.
 */
@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
public annotation class DefaultMemo 