package com.zeoflow.memo.compiler.processing.ksp

import com.google.devtools.ksp.symbol.KSFile
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ElementVisitor
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.type.TypeMirror

/**
 * When generating java code, JavaPoet only provides an API that receives Element.
 * This wrapper class helps us wrap a KSFile as an originating element and KspFiler unwraps it to
 * get the actual KSFile out of it.
 */
internal data class KSFileAsOriginatingElement(
    val ksFile: KSFile
) : Element {
    override fun getAnnotationMirrors(): List<AnnotationMirror> {
        return emptyList()
    }

    override fun <A : Annotation?> getAnnotation(annotationType: Class<A>?): A? {
        return null
    }

    override fun <A : Annotation?> getAnnotationsByType(annotationType: Class<A>): Array<A> {
        @Suppress("UNCHECKED_CAST")
        return arrayOfNulls<Any?>(size = 0) as Array<A>
    }

    override fun asType(): TypeMirror {
        throw UnsupportedOperationException(
            "KSFileAsOriginatingElement cannot be converted to a type"
        )
    }

    override fun getKind(): ElementKind {
        return ElementKind.OTHER
    }

    override fun getModifiers(): Set<Modifier> {
        return emptySet()
    }

    override fun getSimpleName(): Name {
        return NameImpl(ksFile.fileName)
    }

    override fun getEnclosingElement(): Element? {
        return null
    }

    override fun getEnclosedElements(): List<Element> {
        return emptyList()
    }

    override fun <R : Any?, P : Any?> accept(v: ElementVisitor<R, P>?, p: P): R? {
        return null
    }

    private class NameImpl(private val str: String) : Name, CharSequence by str {
        override fun contentEquals(cs: CharSequence): Boolean {
            return str == cs.toString()
        }
    }
}