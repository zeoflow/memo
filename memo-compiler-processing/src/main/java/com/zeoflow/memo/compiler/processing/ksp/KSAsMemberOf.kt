package com.zeoflow.memo.compiler.processing.ksp

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter

/**
 * Returns the type of a property as if it is member of the given [ksType].
 */
internal fun KSPropertyDeclaration.typeAsMemberOf(ksType: KSType?): KSType {
    val resolved = type.resolve()
    if (isStatic()) {
        // calling as member with a static would throw as it might be a member of the companion
        // object
        return resolved
    }
    if (ksType == null) {
        return resolved
    }
    // see: https://github.com/google/ksp/issues/107
    // as member of might lose the `isError` information hence we should check before calling
    // asMemberOf.
    if (resolved.isError) {
        return resolved
    }
    return this.asMemberOf(
        containing = ksType
    )
}

internal fun KSValueParameter.typeAsMemberOf(
    functionDeclaration: KSFunctionDeclaration,
    ksType: KSType?
): KSType {
    val resolved = type.resolve()
    if (functionDeclaration.isStatic()) {
        // calling as member with a static would throw as it might be a member of the companion
        // object
        return resolved
    }
    if (resolved.isError) {
        // see: https://github.com/google/ksp/issues/107
        // as member of might lose the `isError` information hence we should check before calling
        // asMemberOf.
        return resolved
    }
    if (ksType == null) {
        return resolved
    }
    val asMember = functionDeclaration.asMemberOf(
        containing = ksType
    )
    // TODO b/173224718
    // this is counter intuitive, we should remove asMemberOf from method parameters.
    val myIndex = functionDeclaration.parameters.indexOf(this)
    return asMember.parameterTypes[myIndex] ?: resolved
}

internal fun KSFunctionDeclaration.returnTypeAsMemberOf(
    ksType: KSType?
): KSType {
    val resolved = returnType?.resolve()
    return when {
        resolved == null -> null
        ksType == null -> resolved
        resolved.isError -> resolved
        isStatic() -> {
            // calling as member with a static would throw as it might be a member of the companion
            // object
            resolved
        }
        else -> this.asMemberOf(
            containing = ksType
        ).returnType
    } ?: error("cannot find return type for $this")
}
