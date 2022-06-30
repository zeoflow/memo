package com.zeoflow.memo.processor.entity

import com.google.common.base.Strings
import com.zeoflow.memo.compiler.processing.XTypeElement
import com.zeoflow.memo.compiler.processing.XVariableElement
import com.zeoflow.memo.compiler.processing.isVariableElement
import com.zeoflow.memo.processor.util.StringUtils
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class EntityData(
    annotatedType: XTypeElement
) {

    val typeElement: XTypeElement
    val packageName: String?
    val entityName: String?

    //    val keyFields: MutableList<PreferenceKeyField?>
    val keyNameFields: MutableList<String?>
    val fields: MutableList<EntityField>

    //    val keyFieldMap: MutableMap<String?, PreferenceKeyField>
    val setterFunctionsList: MutableMap<String?, Element?>
    val getterFunctionsList: MutableMap<String?, Element?>
    val getterCompoundFunctionsList: MutableMap<Array<String>, ExecutableElement>
    var isDefaultPreference = false
    var isEncryption = false
    var isHilt = false
    var encryptionKey: String? = null

    init {
        val memoEntity = annotatedType.getAnnotation(
            com.zeoflow.memo.common.MemoEntity::class
        )
        val defaultMemo = annotatedType.getAnnotation(
            com.zeoflow.memo.common.DefaultMemo::class
        )
        val encryptEntity = annotatedType.getAnnotation(
            com.zeoflow.memo.common.EncryptEntity::class
        )
        val hiltEntity = annotatedType.getAnnotation(
            com.zeoflow.memo.common.Hilt::class
        )

        packageName = annotatedType.packageName
        typeElement = annotatedType
        entityName =
            if (Strings.isNullOrEmpty(memoEntity?.value.toString())) StringUtils.toUpperCamel(
                typeElement.className.simpleName()
            ) else memoEntity?.value.toString()
//        keyFields = ArrayList()
        keyNameFields = ArrayList()
//        keyFieldMap = HashMap()
        setterFunctionsList = HashMap()
        getterFunctionsList = HashMap()
        getterCompoundFunctionsList = HashMap()

        if (defaultMemo != null) isDefaultPreference = true

        fields = mutableListOf()
        println("> EntityClass :prepare-entity: ** ${typeElement.className.simpleName()} **")
        for(field in typeElement.getAllFieldsIncludingPrivateSupers()) {
            fields.add(EntityField(field))
        }

        if (encryptEntity != null) {
            isEncryption = true
            encryptionKey = encryptEntity.value.value
        }

        if (hiltEntity != null) {
            isHilt = true
        }
//
//        if (Strings.isNullOrEmpty(entityName)) {
//            throw VerifyException("You should entity MemoStorage class value.")
//        }
    }

}