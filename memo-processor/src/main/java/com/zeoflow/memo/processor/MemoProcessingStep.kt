package com.zeoflow.memo.processor

import com.google.common.base.VerifyException
import com.squareup.javapoet.JavaFile
import com.zeoflow.memo.compiler.processing.*
import com.zeoflow.memo.processor.entity.EntityBuilder
import com.zeoflow.memo.processor.entity.EntityBuilderJava
import com.zeoflow.memo.processor.entity.EntityData
import com.zeoflow.memo.processor.util.StringUtils
import javax.tools.Diagnostic

class MemoProcessingStep(var processingMachine: ProcessingMachine) : XProcessingStep {

    private val processingEnv: XProcessingEnv = processingMachine.processingEnv
    private val types: ClassTypes = processingMachine.types()
    private val messager: XMessager = processingEnv.messager

    override fun process(
        env: XProcessingEnv,
        elementsByAnnotation: Map<String, Set<XElement>>
    ): Set<XTypeElement> {
        if (elementsByAnnotation.isEmpty()) {
            return mutableSetOf()
        }

        val rejectedElements = mutableSetOf<XTypeElement>()
        val memoEntities = elementsByAnnotation[
                com.zeoflow.memo.common.MemoEntity::class.qualifiedName
        ]?.filterIsInstance<XTypeElement>() ?: emptyList()
        val memoComponents = elementsByAnnotation[
                com.zeoflow.memo.common.MemoComponent::class.qualifiedName
        ]?.filterIsInstance<XTypeElement>() ?: emptyList()
        val injectPreferences = elementsByAnnotation[
                com.zeoflow.memo.common.InjectPreference::class.qualifiedName
        ]?.filterIsInstance<XTypeElement>() ?: emptyList()

        processMemoEntities(memoEntities)

        return rejectedElements
    }

    override fun annotations(): Set<String> {
        val supportedTypes: MutableSet<String> = HashSet()
        supportedTypes.add(com.zeoflow.memo.common.MemoEntity::class.qualifiedName!!)
        supportedTypes.add(com.zeoflow.memo.common.DefaultMemo::class.qualifiedName!!)
        supportedTypes.add(com.zeoflow.memo.common.KeyName::class.qualifiedName!!)
        supportedTypes.add(com.zeoflow.memo.common.InjectPreference::class.qualifiedName!!)
        return supportedTypes
    }

    private fun processMemoEntities(memoEntities: List<XTypeElement>) {
        for (entity in memoEntities) {
            try {
                checkValidEntityType(entity)
                processEntity(entity)
            } catch (e: IllegalAccessException) {
                showErrorLog(e.message, entity)
            }
        }
    }

    @Throws(IllegalAccessException::class)
    private fun checkValidEntityType(annotatedType: XTypeElement) {
        if (!annotatedType.isClass()) {
            throw IllegalAccessException("Only classes can be annotated with @MemoEntity")
        } else if (annotatedType.isPrivate()) {
            showErrorLog("class modifier should not be private", annotatedType)
        }
    }

    @Throws(VerifyException::class)
    private fun processEntity(annotatedType: XTypeElement) {
        try {

            if (processingMachine.language() == Language.JAVA) {
                val entityData = EntityData(
                    annotatedType
                )
                JavaFile.builder(
                    annotatedType.packageName,
                    EntityBuilderJava(entityData, types).generate()
                ).build().writeTo(processingEnv.filer)
            } else if (processingMachine.language() == Language.KOTLIN) {
                val entityData = EntityData(
                    annotatedType
                )
                EntityBuilder(entityData, types)
                    .generate(processingEnv.filer)
            }
        } catch (e: VerifyException) {
            showErrorLog(e.message, annotatedType)
            e.printStackTrace()
        }
    }

    private fun showErrorLog(message: String?, element: XElement) {
        messager.printMessage(
            Diagnostic.Kind.ERROR,
            StringUtils.errorMessagePrefix + message,
            element
        )
    }
}