package com.zeoflow.memo.compiler.processing.ksp.synthetic

import com.zeoflow.memo.compiler.processing.XAnnotated
import com.zeoflow.memo.compiler.processing.XEquality
import com.zeoflow.memo.compiler.processing.XExecutableParameterElement
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE
import com.zeoflow.memo.compiler.processing.ksp.KspExecutableElement
import com.zeoflow.memo.compiler.processing.ksp.KspProcessingEnv
import com.zeoflow.memo.compiler.processing.ksp.KspType
import com.zeoflow.memo.compiler.processing.ksp.requireContinuationClass
import com.zeoflow.memo.compiler.processing.ksp.returnTypeAsMemberOf
import com.zeoflow.memo.compiler.processing.ksp.swapResolvedType
import com.google.devtools.ksp.symbol.Variance

/**
 * XProcessing adds an additional argument to each suspend function for the continiuation because
 * this is what KAPT generates and Depot needs it as long as it generates java code.
 */
internal class KspSyntheticContinuationParameterElement(
    private val env: KspProcessingEnv,
    private val containing: KspExecutableElement
) : XExecutableParameterElement,
    XEquality,
    XAnnotated by KspAnnotated.create(
        env = env,
        delegate = null, // does not matter, this is synthetic and has no annotations.
        filter = NO_USE_SITE
    ) {

    // todo constantValueData
    override val constantValue: Any
        get() = TODO("Not yet implemented")

    override val name: String by lazy {
        // kotlin names this as pN where N is the # of arguments
        // seems like kapt doesn't handle conflicts with declared arguments but we should
        val desiredName = "p${containing.declaration.parameters.size}"

        if (containing.declaration.parameters.none { it.name?.asString() == desiredName }) {
            desiredName
        } else {
            "_syntheticContinuation"
        }
    }

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf("continuation", containing)
    }

    override val type: XType by lazy {
        val continuation = env.resolver.requireContinuationClass()
        val contType = continuation.asType(
            listOf(
                env.resolver.getTypeArgument(
                    checkNotNull(containing.declaration.returnType) {
                        "cannot find return type for $this"
                    },
                    Variance.CONTRAVARIANT
                )
            )
        )
        env.wrap(
            ksType = contType,
            allowPrimitives = false
        )
    }

    override val fallbackLocationText: String
        get() = "return type of ${containing.fallbackLocationText}"

    // Not applicable
    override val docComment: String? get() = null

    override fun asMemberOf(other: XType): XType {
        check(other is KspType)
        val continuation = env.resolver.requireContinuationClass()
        val asMember = containing.declaration.returnTypeAsMemberOf(
            ksType = other.ksType
        )
        val returnTypeRef = checkNotNull(containing.declaration.returnType) {
            "cannot find return type reference for $this"
        }
        val returnTypeAsTypeArgument = env.resolver.getTypeArgument(
            returnTypeRef.swapResolvedType(asMember),
            Variance.CONTRAVARIANT
        )
        val contType = continuation.asType(listOf(returnTypeAsTypeArgument))
        return env.wrap(
            ksType = contType,
            allowPrimitives = false
        )
    }

    override fun kindName(): String {
        return "synthetic continuation parameter"
    }

    override fun equals(other: Any?): Boolean {
        return XEquality.equals(this, other)
    }

    override fun hashCode(): Int {
        return XEquality.hashCode(equalityItems)
    }
}
