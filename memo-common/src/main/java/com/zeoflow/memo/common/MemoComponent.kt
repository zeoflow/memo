package com.zeoflow.memo.common

import kotlin.reflect.KClass

/**
 * Marks a class as a component of PreferenceRooms.
 */
@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
public annotation class MemoComponent(
    /**
     * Declaring the entities for the component.
     *
     * @return entity classes.
     */
    val entities: Array<KClass<*>> = []
)