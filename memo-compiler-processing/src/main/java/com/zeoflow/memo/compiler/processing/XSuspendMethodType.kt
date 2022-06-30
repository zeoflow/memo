package com.zeoflow.memo.compiler.processing

interface XSuspendMethodType : XMethodType {
    /**
     * IfReturns the real return type as seen by Kotlin.
     */
    fun getSuspendFunctionReturnType(): XType
}