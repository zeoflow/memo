package com.zeoflow.memo.processor.entity

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.zeoflow.memo.common.*
import com.zeoflow.memo.compiler.processing.XFieldElement
import com.zeoflow.memo.compiler.processing.XType
import com.zeoflow.memo.processor.poet.getType

class EntityField(var xFieldElement: XFieldElement) {

    var defaultType: XType? = null
    var functionsType: XType? = null
    val className: ClassName
    val keyName: String
    val isObservable: Boolean
    val isListener: Boolean

    init {
        keyName = if (xFieldElement.hasAnnotation(KeyName::class)) {
            val keyNameAnnotation = xFieldElement.getAnnotation(KeyName::class)!!.value
            keyNameAnnotation.value
        } else {
            xFieldElement.name
        }
        className = getType(xFieldElement.type.typeName)
        isObservable = xFieldElement.hasAnnotation(Observable::class)
        isListener = xFieldElement.hasAnnotation(Listener::class)
        if (xFieldElement.hasAnnotation(Default::class)) {
            val xType = xFieldElement.getAnnotation(Default::class)!!.getAsType("value")!!
            defaultType = xType
        }
        if (xFieldElement.hasAnnotation(CompoundFunction::class)) {
            val xType = xFieldElement.getAnnotation(CompoundFunction::class)!!.getAsType("value")!!
            functionsType = xType
        }
    }

}