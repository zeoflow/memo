package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XElement
import com.zeoflow.memo.compiler.processing.XEquality
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import java.util.Locale

internal abstract class KspElement(
    protected val env: KspProcessingEnv,
    open val declaration: KSAnnotated
) : XElement, XEquality {
    override fun kindName(): String {
        return when (declaration) {
            is KSClassDeclaration ->
                (declaration as KSClassDeclaration).classKind.name
                    .lowercase(Locale.US)
            is KSPropertyDeclaration -> "property"
            is KSFunctionDeclaration -> "function"
            else -> declaration::class.simpleName ?: "unknown"
        }
    }

    override fun equals(other: Any?): Boolean {
        return XEquality.equals(this, other)
    }

    override fun hashCode(): Int {
        return XEquality.hashCode(equalityItems)
    }

    override fun toString(): String {
        return declaration.toString()
    }

    /**
     * Return a reference to the containing file that implements the
     * [javax.lang.model.element.Element] API so that we can report it to JavaPoet.
     */
    fun containingFileAsOriginatingElement(): KSFileAsOriginatingElement? {
        return (declaration as? KSDeclaration)?.containingFile?.let {
            KSFileAsOriginatingElement(it)
        }
    }

    override val docComment: String? by lazy {
        (declaration as? KSDeclaration)?.docString
    }
}