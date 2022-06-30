package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XExecutableParameterElement
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmType
import com.google.auto.common.MoreTypes
import javax.lang.model.element.VariableElement

internal abstract class JavacVariableElement(
    env: JavacProcessingEnv,
    val containing: JavacTypeElement,
    override val element: VariableElement
) : JavacElement(env, element), XExecutableParameterElement {

    abstract val kotlinType: KmType?

    // todo constantValueData
    override val constantValue: Any?
        get() = element.constantValue

    override val name: String
        get() = element.simpleName.toString()

    override val type: JavacType by lazy {
        MoreTypes.asMemberOf(env.typeUtils, containing.type.typeMirror, element).let {
            env.wrap<JavacType>(
                typeMirror = it,
                kotlinType = kotlinType,
                elementNullability = element.nullability
            )
        }
    }

    override fun asMemberOf(other: XType): JavacType {
        return if (containing.type.isSameType(other)) {
            type
        } else {
            check(other is JavacDeclaredType)
            val asMember = MoreTypes.asMemberOf(env.typeUtils, other.typeMirror, element)
            env.wrap<JavacType>(
                typeMirror = asMember,
                kotlinType = kotlinType,
                elementNullability = element.nullability
            )
        }
    }

    override val equalityItems: Array<out Any?> by lazy {
        arrayOf(element, containing)
    }
}
