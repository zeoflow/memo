package com.zeoflow.memo.compiler.processing.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSReferenceElement
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitor
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.NonExistLocation
import com.google.devtools.ksp.symbol.Origin

/**
 * Creates a new TypeReference from [this] where the resolved type [replacement] but everything
 * else is the same (e.g. location).
 */
internal fun KSTypeReference.swapResolvedType(replacement: KSType): KSTypeReference {
    return DelegatingTypeReference(
        original = this,
        resolved = replacement
    )
}

/**
 * Creates a [NonExistLocation] type reference for [this].
 */
internal fun KSType.createTypeReference(): KSTypeReference {
    return NoLocationTypeReference(this)
}

private class DelegatingTypeReference(
    val original: KSTypeReference,
    val resolved: KSType
) : KSTypeReference by original {
    override fun resolve() = resolved
}

private class NoLocationTypeReference(
    val resolved: KSType
) : KSTypeReference {
    override val annotations: Sequence<KSAnnotation>
        get() = emptySequence()
    override val element: KSReferenceElement?
        get() = null
    override val location: Location
        get() = NonExistLocation
    override val modifiers: Set<Modifier>
        get() = emptySet()
    override val origin: Origin
        get() = Origin.SYNTHETIC

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitTypeReference(this, data)
    }

    override fun resolve(): KSType = resolved
}