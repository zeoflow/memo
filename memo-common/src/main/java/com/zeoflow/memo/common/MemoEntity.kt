package com.zeoflow.memo.common

/**
 * Marks a class as an SharedPreference data. This class will have a mapping SharedPreference with
 * Upper camel case.
 */
@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
public annotation class MemoEntity(
    /**
     * Preference entity name of the SharedPreference persistence. w
     *
     *
     * If not used, the class generated entity class name will be upper camel case of the class
     * name.
     *
     * @return The Preference name of the SharedPreference entity.
     */
    val value: String = ""
)