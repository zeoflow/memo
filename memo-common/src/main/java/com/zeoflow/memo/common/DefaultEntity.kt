package com.zeoflow.memo.common

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public annotation class DefaultEntity(
    val value: KClass<*>
)