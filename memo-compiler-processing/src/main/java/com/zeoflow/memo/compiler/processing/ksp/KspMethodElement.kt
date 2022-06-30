package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XExecutableParameterElement
import com.zeoflow.memo.compiler.processing.XMethodElement
import com.zeoflow.memo.compiler.processing.XMethodType
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.XTypeElement
import com.zeoflow.memo.compiler.processing.ksp.synthetic.KspSyntheticContinuationParameterElement
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal sealed class KspMethodElement(
    env: KspProcessingEnv,
    containing: KspMemberContainer,
    declaration: KSFunctionDeclaration
) : KspExecutableElement(
    env = env,
    containing = containing,
    declaration = declaration
),
    XMethodElement {

    override val name: String by lazy {
        env.resolver.safeGetJvmName(declaration)
    }

    override val executableType: XMethodType by lazy {
        KspMethodType.create(
            env = env,
            origin = this,
            containing = this.containing.type
        )
    }

    override fun isJavaDefault(): Boolean {
        return declaration.modifiers.contains(Modifier.JAVA_DEFAULT) ||
            declaration.hasJvmDefaultAnnotation()
    }

    override fun asMemberOf(other: XType): XMethodType {
        check(other is KspType)
        return KspMethodType.create(
            env = env,
            origin = this,
            containing = other
        )
    }

    override fun hasKotlinDefaultImpl(): Boolean {
        val parentDeclaration = declaration.parentDeclaration
        // if parent declaration is an interface and we are not marked as an abstract method,
        // we should have a default implementation
        return parentDeclaration is KSClassDeclaration &&
            parentDeclaration.classKind == ClassKind.INTERFACE &&
            !declaration.isAbstract
    }

    override fun overrides(other: XMethodElement, owner: XTypeElement): Boolean {
        return env.resolver.overrides(this, other)
    }

    override fun copyTo(newContainer: XTypeElement): KspMethodElement {
        check(newContainer is KspTypeElement)
        return create(
            env = env,
            containing = newContainer,
            declaration = declaration
        )
    }

    private class KspNormalMethodElement(
        env: KspProcessingEnv,
        containing: KspMemberContainer,
        declaration: KSFunctionDeclaration
    ) : KspMethodElement(
        env, containing, declaration
    ) {
        override val returnType: XType by lazy {
            // b/160258066
            // we may need to box the return type if it is overriding a generic, hence, we should
            // use the declaration of the overridee if available when deciding nullability
            val overridee = declaration.findOverridee()
            env.wrap(
                ksType = declaration.returnTypeAsMemberOf(
                    ksType = containing.type?.ksType
                ),
                originatingReference = checkNotNull(overridee?.returnType ?: declaration.returnType)
            )
        }
        override fun isSuspendFunction() = false
    }

    private class KspSuspendMethodElement(
        env: KspProcessingEnv,
        containing: KspMemberContainer,
        declaration: KSFunctionDeclaration
    ) : KspMethodElement(
        env, containing, declaration
    ) {
        override fun isSuspendFunction() = true

        override val returnType: XType by lazy {
            env.wrap(
                ksType = env.resolver.builtIns.anyType.makeNullable(),
                allowPrimitives = false
            )
        }

        override val parameters: List<XExecutableParameterElement>
            get() = super.parameters + KspSyntheticContinuationParameterElement(
                env = env,
                containing = this
            )
    }

    companion object {
        fun create(
            env: KspProcessingEnv,
            containing: KspMemberContainer,
            declaration: KSFunctionDeclaration
        ): KspMethodElement {
            return if (declaration.modifiers.contains(Modifier.SUSPEND)) {
                KspSuspendMethodElement(env, containing, declaration)
            } else {
                KspNormalMethodElement(env, containing, declaration)
            }
        }
    }
}
