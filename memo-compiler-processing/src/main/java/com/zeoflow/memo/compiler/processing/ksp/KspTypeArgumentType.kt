package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XNullability
import com.zeoflow.memo.compiler.processing.XType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.javapoet.TypeName

/**
 * The typeName for type arguments requires the type parameter, hence we have a special type
 * for them when we produce them.
 */
internal class KspTypeArgumentType(
    env: KspProcessingEnv,
    val typeParam: KSTypeParameter,
    val typeArg: KSTypeArgument
) : KspType(
    env = env,
    ksType = typeArg.requireType()
) {
    /**
     * When KSP resolves classes, it always resolves to the upper bound. Hence, the ksType we
     * pass to super is actually our extendsBound.
     */
    private val _extendsBound by lazy {
        env.wrap(
            ksType = ksType,
            allowPrimitives = false
        )
    }

    override val typeName: TypeName by lazy {
        typeArg.typeName(typeParam, env.resolver)
    }

    override fun boxed(): KspTypeArgumentType {
        return this
    }

    override fun extendsBound(): XType {
        return _extendsBound
    }

    override fun copyWithNullability(nullability: XNullability): KspTypeArgumentType {
        return KspTypeArgumentType(
            env = env,
            typeParam = typeParam,
            typeArg = DelegatingTypeArg(
                original = typeArg,
                type = ksType.withNullability(nullability).createTypeReference()
            )
        )
    }

    private class DelegatingTypeArg(
        val original: KSTypeArgument,
        override val type: KSTypeReference
    ) : KSTypeArgument by original
}
