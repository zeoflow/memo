package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XFieldElement
import com.zeoflow.memo.compiler.processing.XHasModifiers
import com.zeoflow.memo.compiler.processing.XTypeElement
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmProperty
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmType
import javax.lang.model.element.VariableElement

internal class JavacFieldElement(
    env: JavacProcessingEnv,
    containing: JavacTypeElement,
    element: VariableElement
) : JavacVariableElement(env, containing, element),
    XFieldElement,
    XHasModifiers by JavacHasModifiers(element) {

    private val kotlinMetadata: KmProperty? by lazy {
        (enclosingElement as? JavacTypeElement)?.kotlinMetadata?.getPropertyMetadata(name)
    }

    override val kotlinType: KmType?
        get() = kotlinMetadata?.type

    override val enclosingElement: XTypeElement by lazy {
        element.requireEnclosingType(env)
    }
}
