package com.zeoflow.memo.compiler.processing

/**
 * Represents a constructor of a class.
 *
 * @see XMethodElement
 * @see XExecutableElement
 */
interface XConstructorElement : XExecutableElement {
    override val enclosingElement: XTypeElement

    override val fallbackLocationText: String
        get() = buildString {
            append(enclosingElement.qualifiedName)
            append(".<init>")
            append("(")
            append(
                parameters.joinToString(", ") {
                    it.type.typeName.toString()
                }
            )
            append(")")
        }
}
