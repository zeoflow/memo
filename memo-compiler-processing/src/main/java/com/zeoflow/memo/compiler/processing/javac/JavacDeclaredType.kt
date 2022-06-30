package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XNullability
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmType
import javax.lang.model.type.DeclaredType

/**
 * Declared types are different from non declared types in java (e.g. primitives, or wildcard
 * types). Even thought XProcessing does not distinguish between these these, in the java
 * implementation, it is handy to have a separate type for explicit typeMirror information.
 */
internal class JavacDeclaredType private constructor(
    env: JavacProcessingEnv,
    override val typeMirror: DeclaredType,
    override val nullability: XNullability,
    override val kotlinType: KmType?
) : JavacType(
    env, typeMirror
) {
    constructor(
        env: JavacProcessingEnv,
        typeMirror: DeclaredType,
        kotlinType: KmType
    ) : this(
        env = env,
        typeMirror = typeMirror,
        nullability = kotlinType.nullability,
        kotlinType = kotlinType
    )

    constructor(
        env: JavacProcessingEnv,
        typeMirror: DeclaredType,
        nullability: XNullability
    ) : this(
        env = env,
        typeMirror = typeMirror,
        nullability = nullability,
        kotlinType = null
    )

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf(typeMirror)
    }

    override val typeArguments: List<JavacType> by lazy {
        typeMirror.typeArguments.mapIndexed { index, typeMirror ->
            env.wrap<JavacType>(
                typeMirror = typeMirror,
                kotlinType = kotlinType?.typeArguments?.getOrNull(index),
                elementNullability = XNullability.UNKNOWN
            )
        }
    }

    override fun copyWithNullability(nullability: XNullability): JavacDeclaredType {
        return JavacDeclaredType(
            env = env,
            typeMirror = typeMirror,
            kotlinType = kotlinType,
            nullability = nullability
        )
    }
}
