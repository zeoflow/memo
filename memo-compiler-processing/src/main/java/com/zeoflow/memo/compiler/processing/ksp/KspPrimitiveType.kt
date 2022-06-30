package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XNullability
import com.zeoflow.memo.compiler.processing.tryUnbox
import com.google.devtools.ksp.symbol.KSType
import com.squareup.javapoet.TypeName

/**
 * This tries to mimic primitive types in Kotlin.
 *
 * Primitiveness of a type cannot always be driven from itself (e.g. its nullability).
 * For instance, a kotlin.Int might be non-null but still be non primitive if it is derived from a
 * generic type argument or is part of type parameters.
 */
internal class KspPrimitiveType(
    env: KspProcessingEnv,
    ksType: KSType
) : KspType(env, ksType) {
    override val typeName: TypeName
        get() = ksType.typeName(env.resolver).tryUnbox()

    override fun boxed(): KspType {
        return env.wrap(
            ksType = ksType,
            allowPrimitives = false
        )
    }

    override fun copyWithNullability(nullability: XNullability): KspType {
        return when (nullability) {
            XNullability.NONNULL -> {
                this
            }
            XNullability.NULLABLE -> {
                // primitive types cannot be nullable hence we box them.
                boxed().makeNullable()
            }
            else -> {
                // this should actually never happens as the only time this is called is from
                // make nullable-make nonnull but we have this error here for completeness.
                error("cannot set nullability to unknown in KSP")
            }
        }
    }
}
