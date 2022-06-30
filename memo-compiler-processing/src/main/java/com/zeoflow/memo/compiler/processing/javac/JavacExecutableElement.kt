package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XExecutableElement
import com.zeoflow.memo.compiler.processing.XHasModifiers
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmExecutable
import com.zeoflow.memo.compiler.processing.javac.kotlin.descriptor
import javax.lang.model.element.ExecutableElement

internal abstract class JavacExecutableElement(
    env: JavacProcessingEnv,
    val containing: JavacTypeElement,
    override val element: ExecutableElement
) : JavacElement(
    env,
    element
),
    XExecutableElement,
    XHasModifiers by JavacHasModifiers(element) {
    abstract val kotlinMetadata: KmExecutable?

    val descriptor by lazy {
        element.descriptor()
    }

    override val parameters: List<JavacVariableElement> by lazy {
        element.parameters.mapIndexed { index, variable ->
            JavacMethodParameter(
                env = env,
                executable = this,
                containing = containing,
                element = variable,
                kotlinMetadata = kotlinMetadata?.parameters?.getOrNull(index)
            )
        }
    }

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf(element, containing)
    }

    override fun isVarArgs(): Boolean {
        return element.isVarArgs
    }

    companion object {
        internal const val DEFAULT_IMPLS_CLASS_NAME = "DefaultImpls"
    }
}
