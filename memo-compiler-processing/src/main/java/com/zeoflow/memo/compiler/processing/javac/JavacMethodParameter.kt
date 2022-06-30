package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.javac.kotlin.KmType
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmValueParameter
import javax.lang.model.element.VariableElement

internal class JavacMethodParameter(
    env: JavacProcessingEnv,
    private val executable: JavacExecutableElement,
    containing: JavacTypeElement,
    element: VariableElement,
    val kotlinMetadata: KmValueParameter?
) : JavacVariableElement(env, containing, element) {
    override val name: String
        get() = kotlinMetadata?.name ?: super.name
    override val kotlinType: KmType?
        get() = kotlinMetadata?.type
    override val fallbackLocationText: String
        get() = if (executable is JavacMethodElement && executable.isSuspendFunction() &&
            this === executable.parameters.last()
        ) {
            "return type of ${executable.fallbackLocationText}"
        } else {
            "$name in ${executable.fallbackLocationText}"
        }
}
