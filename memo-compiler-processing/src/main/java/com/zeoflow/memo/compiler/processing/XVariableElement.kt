package com.zeoflow.memo.compiler.processing

/**
 * Represents a variable element, that is either a method parameter or a field.
 */
interface XVariableElement : XElement {

    /**
     * The value for the variable element.
     */
    val constantValue: Any?

    /**
     * The name of the variable element.
     */
    val name: String

    /**
     * Returns the type of this field or parameter
     */
    val type: XType

    /**
     * Returns this type as a member of the [other] type.
     * It is useful when this [XVariableElement] has a generic type declaration and its type is
     * specified in [other]. (e.g. Bar<T> vs Foo : Bar<String>)
     */
    fun asMemberOf(other: XType): XType
}