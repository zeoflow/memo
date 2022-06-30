package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XNullability
import com.google.devtools.ksp.symbol.KSType
import com.squareup.javapoet.TypeName

/**
 * Representation of `void` in KSP.
 *
 * By default, kotlin.Unit is a valid type in jvm and does not get auto-converted to void (unlike
 * kotlin.Int etc). For those cases, KspProcessingEnv uses this type to properly represent java
 * void in Kotlin so that Depot can generate the correct java code.
 */
internal class KspVoidType(
    env: KspProcessingEnv,
    ksType: KSType,
    val boxed: Boolean
) : KspType(env, ksType) {
    override val typeName: TypeName
        get() = if (boxed || nullability == XNullability.NULLABLE) {
            TypeName.VOID.box()
        } else {
            TypeName.VOID
        }

    override fun boxed(): KspType {
        return if (boxed) {
            this
        } else {
            KspVoidType(
                env = env,
                ksType = ksType,
                boxed = true
            )
        }
    }

    override fun copyWithNullability(nullability: XNullability): KspType {
        return KspVoidType(
            env = env,
            ksType = ksType.withNullability(nullability),
            boxed = boxed || nullability == XNullability.NULLABLE
        )
    }
}
