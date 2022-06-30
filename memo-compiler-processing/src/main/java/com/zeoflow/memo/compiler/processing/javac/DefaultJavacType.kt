package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XNullability
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmType
import javax.lang.model.type.TypeMirror

/**
 * Catch-all class for XType implementation when we don't need/discover a sub-type
 */
internal class DefaultJavacType private constructor(
    env: JavacProcessingEnv,
    typeMirror: TypeMirror,
    override val nullability: XNullability,
    override val kotlinType: KmType?
) : JavacType(
    env, typeMirror
) {
    constructor(
        env: JavacProcessingEnv,
        typeMirror: TypeMirror,
        kotlinType: KmType
    ) : this(
        env = env,
        typeMirror = typeMirror,
        nullability = kotlinType.nullability,
        kotlinType = kotlinType
    )

    constructor(
        env: JavacProcessingEnv,
        typeMirror: TypeMirror,
        nullability: XNullability
    ) : this(
        env = env,
        typeMirror = typeMirror,
        nullability = nullability,
        kotlinType = null
    )

    override val equalityItems by lazy {
        arrayOf(typeMirror)
    }

    override val typeArguments: List<XType>
        /**
         * This is always empty because if the type mirror is declared, we wrap it in a
         * JavacDeclaredType.
         */
        get() = emptyList()

    override fun copyWithNullability(nullability: XNullability): JavacType {
        return DefaultJavacType(
            env = env,
            typeMirror = typeMirror,
            kotlinType = kotlinType,
            nullability = nullability
        )
    }
}