package com.zeoflow.memo.compiler.processing

/**
 * This wraps an annotation element that is both accessible from the processor and runtime.
 *
 * It won't scale to a general purpose processing APIs where an equivelant of the AnnotationMirror
 * API needs to be provided but works well for Depot's case.
 */
interface XAnnotationBox<T> {
    /**
     * The value field of the annotation
     */
    val value: T

    /**
     * Returns the value of the given [methodName] as a type reference.
     */
    fun getAsType(methodName: String): XType?

    /**
     * Returns the value of the given [methodName] as a list of type references.
     */
    fun getAsTypeList(methodName: String): List<XType>

    /**
     * Returns the value of the given [methodName] as another boxed annotation.
     */
    fun <T : Annotation> getAsAnnotationBox(methodName: String): XAnnotationBox<T>

    /**
     * Returns the value of the given [methodName] as an array of boxed annotations.
     */
    fun <T : Annotation> getAsAnnotationBoxArray(methodName: String): Array<XAnnotationBox<T>>
}