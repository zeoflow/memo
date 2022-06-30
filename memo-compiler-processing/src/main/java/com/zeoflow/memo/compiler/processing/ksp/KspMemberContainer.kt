package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XMemberContainer
import com.google.devtools.ksp.symbol.KSDeclaration

internal interface KspMemberContainer : XMemberContainer {
    override val type: KspType?
    val declaration: KSDeclaration?
}