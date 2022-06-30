package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XRawType
import com.zeoflow.memo.compiler.processing.safeTypeName
import com.squareup.javapoet.TypeName

internal class JavacRawType(
    env: JavacProcessingEnv,
    original: JavacType
) : XRawType {
    private val erased = env.typeUtils.erasure(original.typeMirror)
    private val typeUtils = env.delegate.typeUtils

    override val typeName: TypeName = erased.safeTypeName()

    override fun isAssignableFrom(other: XRawType): Boolean {
        return other is JavacRawType && typeUtils.isAssignable(other.erased, erased)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || typeName == (other as? XRawType)?.typeName
    }

    override fun hashCode(): Int {
        return typeName.hashCode()
    }

    override fun toString(): String {
        return erased.toString()
    }
}