package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XElement
import com.zeoflow.memo.compiler.processing.XRoundEnv
import com.zeoflow.memo.compiler.processing.ksp.synthetic.KspSyntheticPropertyMethodElement
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyAccessor
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import kotlin.reflect.KClass

internal class KspRoundEnv(
    private val env: KspProcessingEnv
) : XRoundEnv {
    override val rootElements: Set<XElement>
        get() = TODO("not supported")

    override fun getElementsAnnotatedWith(klass: KClass<out Annotation>): Set<XElement> {
        return getElementsAnnotatedWith(
            annotationQualifiedName = klass.qualifiedName ?: error("No qualified name for $klass")
        )
    }

    override fun getElementsAnnotatedWith(annotationQualifiedName: String): Set<XElement> {
        return env.resolver.getSymbolsWithAnnotation(annotationQualifiedName)
            .map { symbol ->
                when (symbol) {
                    is KSPropertyDeclaration -> {
                        KspFieldElement.create(env, symbol)
                    }
                    is KSClassDeclaration -> {
                        KspTypeElement.create(env, symbol)
                    }
                    is KSFunctionDeclaration -> {
                        KspExecutableElement.create(env, symbol)
                    }
                    is KSPropertyAccessor -> {
                        KspSyntheticPropertyMethodElement.create(env, symbol)
                    }
                    else -> error("Unsupported $symbol with annotation $annotationQualifiedName")
                }
            }.toSet()
    }
}