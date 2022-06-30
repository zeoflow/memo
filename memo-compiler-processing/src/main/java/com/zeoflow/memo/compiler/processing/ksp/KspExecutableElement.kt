package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XAnnotated
import com.zeoflow.memo.compiler.processing.XExecutableElement
import com.zeoflow.memo.compiler.processing.XExecutableParameterElement
import com.zeoflow.memo.compiler.processing.XHasModifiers
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal abstract class KspExecutableElement(
    env: KspProcessingEnv,
    open val containing: KspMemberContainer,
    override val declaration: KSFunctionDeclaration
) : KspElement(
    env = env,
    declaration = declaration
),
    XExecutableElement,
    XHasModifiers by KspHasModifiers.create(declaration),
    XAnnotated by KspAnnotated.create(
        env = env,
        delegate = declaration,
        filter = NO_USE_SITE
    ) {

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf(containing, declaration)
    }

    override val enclosingElement: KspMemberContainer by lazy {
        declaration.requireEnclosingMemberContainer(env)
    }

    override val parameters: List<XExecutableParameterElement> by lazy {
        declaration.parameters.map {
            KspExecutableParameterElement(
                env = env,
                method = this,
                parameter = it
            )
        }
    }

    override fun isVarArgs(): Boolean {
        // in java, only the last argument can be a vararg so for suspend functions, it is never
        // a vararg function. this would change if depot generated kotlin code
        return !declaration.modifiers.contains(Modifier.SUSPEND) &&
            declaration.parameters.any {
                it.isVararg
            }
    }

    companion object {
        fun create(
            env: KspProcessingEnv,
            declaration: KSFunctionDeclaration
        ): KspExecutableElement {
            val enclosingContainer = declaration.findEnclosingMemberContainer(env)

            checkNotNull(enclosingContainer) {
                "XProcessing does not currently support annotations on top level " +
                    "functions with KSP. Cannot process $declaration."
            }

            return when {
                declaration.isConstructor() -> {
                    KspConstructorElement(
                        env = env,
                        containing = enclosingContainer as? KspTypeElement ?: error(
                            "The container for $declaration should be a type element"
                        ),
                        declaration = declaration
                    )
                }
                else -> {
                    KspMethodElement.create(
                        env = env,
                        containing = enclosingContainer,
                        declaration = declaration
                    )
                }
            }
        }
    }
}
