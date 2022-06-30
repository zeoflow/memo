package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XConstructorElement
import com.zeoflow.memo.compiler.processing.XTypeElement
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmConstructor
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

internal class JavacConstructorElement(
    env: JavacProcessingEnv,
    containing: JavacTypeElement,
    element: ExecutableElement
) : JavacExecutableElement(
    env,
    containing,
    element
),
    XConstructorElement {
    init {
        check(element.kind == ElementKind.CONSTRUCTOR) {
            "Constructor element is constructed with invalid type: $element"
        }
    }

    override val enclosingElement: XTypeElement by lazy {
        element.requireEnclosingType(env)
    }

    override val kotlinMetadata: KmConstructor? by lazy {
        (enclosingElement as? JavacTypeElement)?.kotlinMetadata?.getConstructorMetadata(element)
    }
}
