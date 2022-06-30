package com.zeoflow.memo.compiler.processing.ksp.synthetic

import com.zeoflow.memo.compiler.processing.XMethodType
import com.zeoflow.memo.compiler.processing.XType
import com.squareup.javapoet.TypeVariableName

/**
 * @see KspSyntheticPropertyMethodElement
 */
internal sealed class KspSyntheticPropertyMethodType(
    val origin: KspSyntheticPropertyMethodElement,
    val containing: XType?
) : XMethodType {

    override val parameterTypes: List<XType> by lazy {
        if (containing == null) {
            origin.parameters.map {
                it.type
            }
        } else {
            origin.parameters.map {
                it.asMemberOf(containing)
            }
        }
    }

    override val typeVariableNames: List<TypeVariableName>
        get() = emptyList()

    companion object {
        fun create(
            element: KspSyntheticPropertyMethodElement,
            container: XType?
        ): XMethodType {
            return when (element) {
                is KspSyntheticPropertyMethodElement.Getter ->
                    Getter(
                        origin = element,
                        containingType = container
                    )
                is KspSyntheticPropertyMethodElement.Setter ->
                    Setter(
                        origin = element,
                        containingType = container
                    )
            }
        }
    }

    private class Getter(
        origin: KspSyntheticPropertyMethodElement.Getter,
        containingType: XType?
    ) : KspSyntheticPropertyMethodType(
        origin = origin,
        containing = containingType
    ) {
        override val returnType: XType by lazy {
            if (containingType == null) {
                origin.field.type
            } else {
                origin.field.asMemberOf(containingType)
            }
        }
    }

    private class Setter(
        origin: KspSyntheticPropertyMethodElement.Setter,
        containingType: XType?
    ) : KspSyntheticPropertyMethodType(
        origin = origin,
        containing = containingType
    ) {
        override val returnType: XType
            // setters always return Unit, no need to get it as type of
            get() = origin.returnType
    }
}
