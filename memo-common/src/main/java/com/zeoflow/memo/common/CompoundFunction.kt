package com.zeoflow.memo.common

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
public annotation class CompoundFunction(
    val value: KClass<*>
)