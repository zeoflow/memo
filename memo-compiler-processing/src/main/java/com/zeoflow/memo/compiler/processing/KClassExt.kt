package com.zeoflow.memo.compiler.processing

import kotlin.reflect.KClass

private typealias JavaRepeatable = java.lang.annotation.Repeatable

/**
 * Returns the container annotation if `this` is a Repeatable annotation.
 */
internal val <T : Annotation> KClass<T>.containerAnnotation: KClass<out Annotation>?
    get() = this.java.getAnnotation(JavaRepeatable::class.java)?.value
