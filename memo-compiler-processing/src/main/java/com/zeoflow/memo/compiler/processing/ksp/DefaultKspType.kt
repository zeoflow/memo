package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XNullability
import com.zeoflow.memo.compiler.processing.tryBox
import com.google.devtools.ksp.symbol.KSType
import com.squareup.javapoet.TypeName

internal class DefaultKspType(
    env: KspProcessingEnv,
    ksType: KSType
) : KspType(env, ksType) {
    override val typeName: TypeName by lazy {
        // always box these. For primitives, typeName might return the primitive type but if we
        // wanted it to be a primitive, we would've resolved it to [KspPrimitiveType].
        ksType.typeName(env.resolver).tryBox()
    }

    override fun boxed(): DefaultKspType {
        return this
    }

    override fun copyWithNullability(nullability: XNullability): KspType {
        return DefaultKspType(
            env = env,
            ksType = ksType.withNullability(nullability)
        )
    }
}