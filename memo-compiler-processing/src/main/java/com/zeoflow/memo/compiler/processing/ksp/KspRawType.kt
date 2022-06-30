package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XRawType
import com.zeoflow.memo.compiler.processing.rawTypeName
import com.squareup.javapoet.TypeName
import com.google.devtools.ksp.symbol.KSType

internal class KspRawType private constructor(
    private val ksType: KSType,
    override val typeName: TypeName
) : XRawType {
    constructor(original: KspType) : this(
        ksType = original.ksType.starProjection().makeNotNullable(),
        typeName = original.typeName.rawTypeName()
    )

    override fun isAssignableFrom(other: XRawType): Boolean {
        check(other is KspRawType)
        return ksType.isAssignableFrom(other.ksType)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || typeName == (other as? XRawType)?.typeName
    }

    override fun hashCode(): Int {
        return typeName.hashCode()
    }

    override fun toString(): String {
        return typeName.toString()
    }
}
