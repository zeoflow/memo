package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XConstructorElement
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal class KspConstructorElement(
    env: KspProcessingEnv,
    override val containing: KspTypeElement,
    declaration: KSFunctionDeclaration
) : KspExecutableElement(
    env = env,
    containing = containing,
    declaration = declaration
),
    XConstructorElement {
    override val enclosingElement: KspTypeElement by lazy {
        declaration.requireEnclosingMemberContainer(env) as? KspTypeElement
            ?: error("Constructor parent must be a type element $this")
    }
}
