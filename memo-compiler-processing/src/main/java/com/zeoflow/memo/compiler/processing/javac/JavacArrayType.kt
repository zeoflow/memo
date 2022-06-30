package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XArrayType
import com.zeoflow.memo.compiler.processing.XNullability
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmType
import javax.lang.model.type.ArrayType

internal class JavacArrayType private constructor(
    env: JavacProcessingEnv,
    override val typeMirror: ArrayType,
    override val nullability: XNullability,
    private val knownComponentNullability: XNullability?,
    override val kotlinType: KmType?
) : JavacType(
    env,
    typeMirror
),
    XArrayType {
    constructor(
        env: JavacProcessingEnv,
        typeMirror: ArrayType,
        kotlinType: KmType
    ) : this(
        env = env,
        typeMirror = typeMirror,
        nullability = kotlinType.nullability,
        knownComponentNullability = kotlinType.typeArguments.firstOrNull()?.nullability,
        kotlinType = kotlinType
    )

    constructor(
        env: JavacProcessingEnv,
        typeMirror: ArrayType,
        nullability: XNullability,
        knownComponentNullability: XNullability?
    ) : this(
        env = env,
        typeMirror = typeMirror,
        nullability = nullability,
        knownComponentNullability = knownComponentNullability,
        kotlinType = null
    )

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf(typeMirror)
    }

    override val typeArguments: List<XType>
        get() = emptyList()

    override val componentType: XType by lazy {
        val componentType = typeMirror.componentType
        val componentTypeNullability =
            knownComponentNullability ?: if (componentType.kind.isPrimitive) {
                XNullability.NONNULL
            } else {
                XNullability.UNKNOWN
            }
        env.wrap<JavacType>(
            typeMirror = componentType,
            kotlinType = kotlinType?.typeArguments?.firstOrNull(),
            elementNullability = componentTypeNullability
        )
    }

    override fun copyWithNullability(nullability: XNullability): JavacType {
        return JavacArrayType(
            env = env,
            typeMirror = typeMirror,
            nullability = nullability,
            knownComponentNullability = knownComponentNullability,
            kotlinType = kotlinType
        )
    }
}