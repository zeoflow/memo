package com.zeoflow.memo.compiler.processing.javac

import javax.lang.model.type.TypeMirror
import javax.lang.model.type.WildcardType
import javax.lang.model.util.SimpleTypeVisitor7

internal fun TypeMirror.extendsBound(): TypeMirror? {
    return this.accept(
        object : SimpleTypeVisitor7<TypeMirror?, Void?>() {
            override fun visitWildcard(type: WildcardType, ignored: Void?): TypeMirror? {
                return type.extendsBound ?: type.superBound
            }
        },
        null
    )
}
