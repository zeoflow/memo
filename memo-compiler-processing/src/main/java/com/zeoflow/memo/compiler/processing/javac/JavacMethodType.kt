package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XMethodType
import com.zeoflow.memo.compiler.processing.XSuspendMethodType
import com.zeoflow.memo.compiler.processing.XType
import com.google.auto.common.MoreTypes
import com.squareup.javapoet.TypeVariableName
import javax.lang.model.type.ExecutableType

internal sealed class JavacMethodType(
    val env: JavacProcessingEnv,
    val element: JavacMethodElement,
    val executableType: ExecutableType
) : XMethodType {
    override val returnType: JavacType by lazy {
        env.wrap<JavacType>(
            typeMirror = executableType.returnType,
            kotlinType = if (element.isSuspendFunction()) {
                // don't use kotlin metadata for suspend return type since it needs to look like
                // java perspective
                null
            } else {
                element.kotlinMetadata?.returnType
            },
            elementNullability = element.element.nullability
        )
    }

    override val typeVariableNames by lazy {
        executableType.typeVariables.map {
            TypeVariableName.get(it)
        }
    }

    override val parameterTypes: List<JavacType> by lazy {
        executableType.parameterTypes.mapIndexed { index, typeMirror ->
            env.wrap<JavacType>(
                typeMirror = typeMirror,
                kotlinType = element.parameters[index].kotlinType,
                elementNullability = element.parameters[index].element.nullability
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is JavacMethodType) return false
        return executableType == other.executableType
    }

    override fun hashCode(): Int {
        return executableType.hashCode()
    }

    override fun toString(): String {
        return executableType.toString()
    }

    private class NormalMethodType(
        env: JavacProcessingEnv,
        element: JavacMethodElement,
        executableType: ExecutableType
    ) : JavacMethodType(
        env = env,
        element = element,
        executableType = executableType
    )

    private class SuspendMethodType(
        env: JavacProcessingEnv,
        element: JavacMethodElement,
        executableType: ExecutableType
    ) : JavacMethodType(
        env = env,
        element = element,
        executableType = executableType
    ),
        XSuspendMethodType {
        override fun getSuspendFunctionReturnType(): XType {
            // the continuation parameter is always the last parameter of a suspend function and it
            // only has one type parameter, e.g Continuation<? super T>
            val typeParam =
                MoreTypes.asDeclared(executableType.parameterTypes.last()).typeArguments.first()
            // kotlin generates ? extends Foo and we want Foo so get the extends bounds
            val bounded = typeParam.extendsBound() ?: typeParam
            return env.wrap<JavacType>(
                typeMirror = bounded,
                // use kotlin metadata here to get the real type information
                kotlinType = element.kotlinMetadata?.returnType,
                elementNullability = element.element.nullability
            )
        }
    }

    companion object {
        fun create(
            env: JavacProcessingEnv,
            element: JavacMethodElement,
            executableType: ExecutableType
        ): JavacMethodType {
            return if (element.isSuspendFunction()) {
                SuspendMethodType(env, element, executableType)
            } else {
                NormalMethodType(env, element, executableType)
            }
        }
    }
}
