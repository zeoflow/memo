package com.zeoflow.memo.compiler.processing

import com.squareup.javapoet.TypeVariableName
import kotlin.contracts.contract

/**
 * Represents a type information for a method.
 *
 * It is not an XType as it does not represent a class or primitive.
 */
interface XMethodType {
    /**
     * The return type of the method
     */
    val returnType: XType

    /**
     * Parameter types of the method.
     */
    val parameterTypes: List<XType>

    /**
     * Returns the names of [TypeVariableName]s for this executable.
     */
    val typeVariableNames: List<TypeVariableName>
}

/**
 * Returns `true` if this method type represents a suspend function
 */
fun XMethodType.isSuspendFunction(): Boolean {
    contract {
        returns(true) implies (this@isSuspendFunction is XSuspendMethodType)
    }
    return this is XSuspendMethodType
}
