package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XAnnotated
import com.zeoflow.memo.compiler.processing.XExecutableParameterElement
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE_OR_METHOD_PARAMETER
import com.google.devtools.ksp.symbol.KSValueParameter

internal class KspExecutableParameterElement(
    env: KspProcessingEnv,
    val method: KspExecutableElement,
    val parameter: KSValueParameter,
) : KspElement(env, parameter),
    XExecutableParameterElement,
    XAnnotated by KspAnnotated.create(env, parameter, NO_USE_SITE_OR_METHOD_PARAMETER) {

    override val equalityItems: Array<out Any?>
        get() = arrayOf(method, parameter)

    // todo constantValueData
    override val constantValue: Any
        get() = TODO("Not yet implemented")

    override val name: String
        get() = parameter.name?.asString() ?: "_no_param_name"

    override val type: KspType by lazy {
        parameter.typeAsMemberOf(
            functionDeclaration = method.declaration,
            ksType = method.containing.type?.ksType
        ).let {
            env.wrap(
                originatingReference = parameter.type,
                ksType = it
            )
        }
    }

    override val fallbackLocationText: String
        get() = "$name in ${method.fallbackLocationText}"

    override fun asMemberOf(other: XType): KspType {
        if (method.containing.type?.isSameType(other) != false) {
            return type
        }
        check(other is KspType)
        return parameter.typeAsMemberOf(
            functionDeclaration = method.declaration,
            ksType = other.ksType
        ).let {
            env.wrap(
                originatingReference = parameter.type,
                ksType = it
            )
        }
    }

    override fun kindName(): String {
        return "function parameter"
    }
}
