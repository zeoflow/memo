package com.zeoflow.memo.compiler.processing

import kotlin.reflect.KClass

/**
 * Internal API for [XAnnotated] that handles repeated annotations.
 */
internal interface InternalXAnnotated : XAnnotated {
    /**
     * Repeated annotations show up differently between source and .class files.
     *
     * To avoid that inconsistency, [XAnnotated] only provides [XAnnotated.getAnnotations] and in
     * this internal wrapper, we handle that inconsistency by finding the container class and
     * asking implementers to implement the 2 arg version instead.
     *
     * see: https://github.com/google/ksp/issues/356
     * see: https://github.com/google/ksp/issues/358
     * see: https://youtrack.jetbrains.com/issue/KT-12794
     *
     * @param annotation The annotation to query
     * @param containerAnnotation The container annotation of the [annotation] if it is a repeatable
     * annotation.
     *
     * @see hasAnnotation
     */
    fun <T : Annotation> getAnnotations(
        annotation: KClass<T>,
        containerAnnotation: KClass<out Annotation>? = annotation.containerAnnotation
    ): List<XAnnotationBox<T>>

    override fun <T : Annotation> getAnnotations(annotation: KClass<T>) = getAnnotations(
        annotation = annotation,
        containerAnnotation = annotation.containerAnnotation
    )

    override fun hasAnnotation(annotation: KClass<out Annotation>) = hasAnnotation(
        annotation = annotation,
        containerAnnotation = annotation.containerAnnotation
    )

    /**
     * Returns `true` if this element is annotated with the given [annotation].
     *
     * Note that this method should check for both [annotation] and [containerAnnotation] to
     * support repeated annotations.
     *
     * @param annotation The annotation to query
     * @param containerAnnotation The container annotation of the [annotation] if it is a repeatable
     * annotation.
     *
     * @see [toAnnotationBox]
     * @see [hasAnyOf]
     */
    fun hasAnnotation(
        annotation: KClass<out Annotation>,
        containerAnnotation: KClass<out Annotation>? = annotation.containerAnnotation
    ): Boolean
}