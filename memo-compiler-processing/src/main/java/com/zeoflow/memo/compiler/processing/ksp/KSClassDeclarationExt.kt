package com.zeoflow.memo.compiler.processing.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal fun KSClassDeclaration.findCompanionObject(): KSClassDeclaration? {
    return declarations.firstOrNull {
        it is KSClassDeclaration && it.isCompanionObject
    } as? KSClassDeclaration
}
