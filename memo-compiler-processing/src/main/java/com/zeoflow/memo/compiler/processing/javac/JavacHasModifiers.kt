package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XHasModifiers
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

/**
 * Implementation of [XHasModifiers] for java elements
 */
internal class JavacHasModifiers(private val element: Element) : XHasModifiers {

    override fun isPublic(): Boolean {
        return element.modifiers.contains(Modifier.PUBLIC)
    }

    override fun isProtected(): Boolean {
        return element.modifiers.contains(Modifier.PROTECTED)
    }

    override fun isAbstract(): Boolean {
        return element.modifiers.contains(Modifier.ABSTRACT)
    }

    override fun isPrivate(): Boolean {
        return element.modifiers.contains(Modifier.PRIVATE)
    }

    override fun isStatic(): Boolean {
        return element.modifiers.contains(Modifier.STATIC)
    }

    override fun isTransient(): Boolean {
        return element.modifiers.contains(Modifier.TRANSIENT)
    }

    override fun isFinal(): Boolean {
        return element.modifiers.contains(Modifier.FINAL)
    }
}