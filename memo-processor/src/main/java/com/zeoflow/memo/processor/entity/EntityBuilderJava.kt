package com.zeoflow.memo.processor.entity

import androidx.annotation.NonNull
import com.squareup.javapoet.*
import com.squareup.kotlinpoet.FunSpec
import com.zeoflow.memo.processor.ClassTypes
import com.zeoflow.memo.processor.poet.getCustomType
import javax.lang.model.element.Modifier

private const val FIELD_INSTANCE = "instance"
private const val FIELD_ENCRYPTION_KEY = "encryptionKey"

class EntityBuilderJava(
    var entityData: EntityData,
    var types: ClassTypes
) {

    private fun simpleName(): String {
        return entityData.typeElement.className.simpleName()
    }

    private fun getClassName(): String {
        return simpleName() + "Memo"
    }

    private fun packageName(): String {
        return entityData.typeElement.className.packageName()
    }

    fun generate(): TypeSpec {
        val builder = TypeSpec.classBuilder(getClassName())
        builder.addJavadoc(
            "Preference class for \$T\n",
            ClassName.get(
                packageName(),
                getClassName()
            )
        )
        builder.addJavadoc(
            "Generated by Memo's Injector (https://github.com/zeoflow/memo).\n"
        )
        builder.addModifiers(Modifier.PUBLIC)
        builder.addFields(getFieldSpecs())
            .addMethods(addInstancesSpec())
            .addTypes(getOnChangedTypeSpecs())
            .addFields(getOnChangedFieldSpecs())
        if (entityData.isDefaultPreference) {
            builder.addMethods(addDefaultPreferenceConstructorsSpec())
        } else {
            builder.addMethods(addConstructorsSpec())
        }

        for (field in entityData.fields) {
            addFieldObservable(field, builder)
            addFieldGetterAndSetter(field, builder)
        }

        return builder.build()
    }

    private fun addFieldObservable(field: EntityField, builder: TypeSpec.Builder) {
        if (!field.isObservable) {
            return
        }
        val mutableLiveData = types.MutableLiveData() as ClassName
        val mutableOfCustom = ParameterizedTypeName.get(
            mutableLiveData,
            field.xFieldElement.type.typeName
        )

        val fieldSpec = FieldSpec.builder(
            mutableOfCustom,
            "${field.keyName}Observable",
            Modifier.PRIVATE,
            Modifier.FINAL
        )
        fieldSpec.addAnnotation(NonNull::class.java)
        fieldSpec.initializer("new MutableLiveData<>()")
        builder.addField(fieldSpec.build())
    }

    private fun addFieldGetterAndSetter(field: EntityField, builder: TypeSpec.Builder) {

    }

    private fun getFieldSpecs(): List<FieldSpec> {
        val fieldSpecs: MutableList<FieldSpec> = java.util.ArrayList()
        fieldSpecs.add(
            FieldSpec.builder(
                getCustomType(
                    packageName(),
                    getClassName()
                ),
                FIELD_INSTANCE,
                Modifier.PRIVATE,
                Modifier.STATIC
            ).build()
        )
        val encryptionField = FieldSpec.builder(
            String::class.java,
            FIELD_ENCRYPTION_KEY,
            Modifier.PRIVATE,
            Modifier.FINAL
        )
        encryptionField.initializer("\$S", entityData.encryptionKey)
        fieldSpecs.add(encryptionField.build())
        return fieldSpecs
    }

    private fun addInstancesSpec(): List<MethodSpec> {
        val methods: MutableList<MethodSpec> = mutableListOf()
        val instance: MethodSpec =
            MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addAnnotation(NonNull::class.java)
                .addStatement(
                    "if (\$N != null) return \$N",
                    FIELD_INSTANCE,
                    FIELD_INSTANCE
                )
                .addStatement(
                    "\$N = new \$N()",
                    FIELD_INSTANCE,
                    getClassName()
                )
                .addStatement(
                    "return \$N",
                    FIELD_INSTANCE
                )
                .returns(
                    getCustomType(
                        packageName(),
                        getClassName()
                    )
                )
                .build()
        methods.add(instance)
        return methods
    }

    private fun getOnChangedTypeSpecs(): List<TypeSpec> {
        val typeSpecs: MutableList<TypeSpec> = java.util.ArrayList()
//        for (keyField in entityData.keyFields) {
//            if (!keyField.isListener) {
//                continue
//            }
//            val changeListenerGenerator = PreferenceChangeListenerGenerator(keyField)
//            typeSpecs.add(changeListenerGenerator.generateInterface())
//        }
        return typeSpecs
    }

    private fun getOnChangedFieldSpecs(): List<FieldSpec> {
        val fieldSpecs: MutableList<FieldSpec> = java.util.ArrayList()
//        for (keyField in entityData.keyFields) {
//            if (!keyField.isListener) {
//                continue
//            }
//            val changeListenerGenerator = PreferenceChangeListenerGenerator(keyField)
//            fieldSpecs.add(changeListenerGenerator.generateField(getClazzName()))
//        }
        return fieldSpecs
    }

    private fun addDefaultPreferenceConstructorsSpec(): List<MethodSpec> {
        val methods: MutableList<MethodSpec> = ArrayList()
        val autoConstructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addJavadoc("AutoConstructor - the context is retrieved from StorageApplication")
            .addStatement("this(getContext())")
            .build()
        methods.add(autoConstructor)
//        val constructor = MethodSpec.constructorBuilder()
//            .addModifiers(Modifier.PRIVATE)
//            .addParameter(
//                ParameterSpec.builder(
//                    TypeName.get(entityData.typeElement.type), CONSTRUCTOR_CONTEXT)
//                    .addAnnotation(NonNull::class.java)
//                    .build()
//            )
//            .build()
//        methods.add(constructor)
        return methods
    }

    private fun addConstructorsSpec(): List<MethodSpec> {
        val methods: MutableList<MethodSpec> = java.util.ArrayList()
        val constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
        if (entityData.isHilt) {
            constructor.addAnnotation(types.Inject() as ClassName)
        }
        if (entityData.isEncryption) {
            constructor.addStatement(
                "\$T.Companion.init().withEncryption(new \$T(\$N)).build()",
                types.Memo(),
                types.ConcealEncryption(),
                FIELD_ENCRYPTION_KEY
            )
        } else {
            constructor.addStatement(
                "\$T.Companion.init().withEncryption(new \$T()).build()",
                types.Memo(),
                types.NoEncryption()
            )
        }
        methods.add(constructor.build())
        return methods
    }
}