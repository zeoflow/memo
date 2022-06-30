package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XAnnotated
import com.zeoflow.memo.compiler.processing.XFieldElement
import com.zeoflow.memo.compiler.processing.XHasModifiers
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.ksp.KspAnnotated.UseSiteFilter.Companion.NO_USE_SITE_OR_FIELD
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

internal class KspFieldElement(
    env: KspProcessingEnv,
    override val declaration: KSPropertyDeclaration,
    val containing: KspMemberContainer
) : KspElement(env, declaration),
    XFieldElement,
    XHasModifiers by KspHasModifiers.create(declaration),
    XAnnotated by KspAnnotated.create(env, declaration, NO_USE_SITE_OR_FIELD) {

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf(declaration, containing)
    }

    override val enclosingElement: KspMemberContainer by lazy {
        declaration.requireEnclosingMemberContainer(env)
    }

    // todo constantValueData
    override val constantValue: Any
        get() = TODO("Not yet implemented")

    override val name: String by lazy {
        declaration.simpleName.asString()
    }

    override val type: KspType by lazy {
        env.wrap(
            originatingReference = declaration.type,
            ksType = declaration.typeAsMemberOf(containing.type?.ksType)
        )
    }

    override fun asMemberOf(other: XType): XType {
        if (containing.type?.isSameType(other) != false) {
            return type
        }
        check(other is KspType)
        val asMember = declaration.typeAsMemberOf(other.ksType)
        return env.wrap(
            originatingReference = declaration.type,
            ksType = asMember
        )
    }

    fun copyTo(newContaining: KspTypeElement) = KspFieldElement(
        env = env,
        declaration = declaration,
        containing = newContaining
    )

    companion object {
        fun create(
            env: KspProcessingEnv,
            declaration: KSPropertyDeclaration
        ): KspFieldElement {
            return KspFieldElement(
                env = env,
                declaration = declaration,
                containing = declaration.requireEnclosingMemberContainer(env)
            )
        }
    }
}
