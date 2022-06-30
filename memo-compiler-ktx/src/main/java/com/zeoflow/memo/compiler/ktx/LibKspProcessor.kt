package com.zeoflow.memo.compiler.ktx

import com.zeoflow.memo.compiler.processing.XProcessingEnv
import com.zeoflow.memo.compiler.processing.XProcessingStep.Companion.executeInKsp
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.zeoflow.memo.processor.MemoProcessingStep

/**
 * Entry point for processing using KSP.
 */
class LibKspProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val processingEnv = XProcessingEnv.create(
            options,
            resolver,
            codeGenerator,
            logger
        )

        return MemoProcessingStep(KotlinProcessingMachine(processingEnv)).executeInKsp(
            processingEnv
        )
    }

    class Provider : SymbolProcessorProvider {
        override fun create(
            options: Map<String, String>,
            kotlinVersion: KotlinVersion,
            codeGenerator: CodeGenerator,
            logger: KSPLogger
        ): SymbolProcessor {
            return LibKspProcessor(
                options = options,
                codeGenerator = codeGenerator,
                logger = logger
            )
        }
    }
}