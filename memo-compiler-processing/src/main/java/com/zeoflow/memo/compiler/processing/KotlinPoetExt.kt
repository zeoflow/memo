package com.zeoflow.memo.compiler.processing

import com.squareup.kotlinpoet.OriginatingElementsHolder

/**
 * Adds the given element as an originating element for compilation.
 * see [OriginatingElementsHolder.Builder.addOriginatingElement].
 */
fun <T : OriginatingElementsHolder.Builder<T>> T.addOriginatingElement(
    element: XElement
): T {
    element.originatingElementForPoet()?.let(this::addOriginatingElement)
    return this
}