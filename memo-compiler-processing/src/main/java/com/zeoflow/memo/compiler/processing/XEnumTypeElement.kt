package com.zeoflow.memo.compiler.processing

import kotlin.contracts.contract

/**
 * Type elements that represent Enum declarations.
 */
interface XEnumTypeElement : XTypeElement {
    val enumConstantNames: Set<String>
}

fun XTypeElement.isEnum(): Boolean {
    contract {
        returns(true) implies (this@isEnum is XEnumTypeElement)
    }
    return this is XEnumTypeElement
}