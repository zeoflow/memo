package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XElement
import com.zeoflow.memo.compiler.processing.XRoundEnv
import com.google.auto.common.MoreElements
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import kotlin.reflect.KClass

@Suppress("UnstableApiUsage")
internal class JavacRoundEnv(
    private val env: JavacProcessingEnv,
    val delegate: RoundEnvironment
) : XRoundEnv {
    override val rootElements: Set<XElement> by lazy {
        delegate.rootElements.map {
            check(MoreElements.isType(it))
            env.wrapTypeElement(MoreElements.asType(it))
        }.toSet()
    }

    override fun getElementsAnnotatedWith(klass: KClass<out Annotation>): Set<XElement> {
        val elements = delegate.getElementsAnnotatedWith(klass.java)
        return wrapAnnotatedElements(elements, klass.java.canonicalName)
    }

    override fun getElementsAnnotatedWith(annotationQualifiedName: String): Set<XElement> {
        val element = env.elementUtils.getTypeElement(annotationQualifiedName)
            ?: error("Cannot find TypeElement: $annotationQualifiedName")

        val elements = delegate.getElementsAnnotatedWith(element)

        return wrapAnnotatedElements(elements, annotationQualifiedName)
    }

    private fun wrapAnnotatedElements(
        elements: Set<Element>,
        annotationName: String
    ): Set<XElement> {
        return elements.map { env.wrapAnnotatedElement(it, annotationName) }.toSet()
    }
}
