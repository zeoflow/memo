package com.zeoflow.memo.compiler.processing.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyAccessor
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier

/**
 * Finds the class that contains this declaration and throws [IllegalStateException] if it cannot
 * be found.
 * @see [findEnclosingAncestorClassDeclaration]
 */
internal fun KSDeclaration.requireEnclosingMemberContainer(
    env: KspProcessingEnv
): KspMemberContainer {
    return checkNotNull(findEnclosingMemberContainer(env)) {
        "Cannot find required enclosing type for $this"
    }
}

/**
 * Find the class that contains this declaration.
 *
 * Node that this is not necessarily the parent declaration. e.g. when a property is declared in
 * a constructor, its containing type is actual two levels up.
 */
internal fun KSDeclaration.findEnclosingMemberContainer(
    env: KspProcessingEnv
): KspMemberContainer? {
    return findEnclosingAncestorClassDeclaration()?.let {
        env.wrapClassDeclaration(it)
    } ?: this.containingFile?.let {
        env.wrapKSFile(it)
    }
}

private fun KSDeclaration.findEnclosingAncestorClassDeclaration(): KSClassDeclaration? {
    var parent = parentDeclaration
    while (parent != null && parent !is KSClassDeclaration) {
        parent = parent.parentDeclaration
    }
    return parent as? KSClassDeclaration
}

internal fun KSDeclaration.isStatic(): Boolean {
    return modifiers.contains(Modifier.JAVA_STATIC) || hasJvmStaticAnnotation() ||
        when (this) {
            is KSPropertyAccessor -> this.receiver.findEnclosingAncestorClassDeclaration() == null
            is KSPropertyDeclaration -> this.findEnclosingAncestorClassDeclaration() == null
            is KSFunctionDeclaration -> this.findEnclosingAncestorClassDeclaration() == null
            else -> false
        }
}

internal fun KSDeclaration.isTransient(): Boolean {
    return modifiers.contains(Modifier.JAVA_TRANSIENT) || hasJvmTransientAnnotation()
}