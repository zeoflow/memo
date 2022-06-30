package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XAnnotationBox
import com.zeoflow.memo.compiler.processing.XElement
import com.zeoflow.memo.compiler.processing.XEquality
import com.google.auto.common.MoreElements
import com.google.auto.common.MoreElements.isAnnotationPresent
import com.zeoflow.memo.compiler.processing.InternalXAnnotated
import java.util.Locale
import javax.lang.model.element.Element
import kotlin.reflect.KClass

@Suppress("UnstableApiUsage")
internal abstract class JavacElement(
    protected val env: JavacProcessingEnv,
    open val element: Element
) : XElement, XEquality, InternalXAnnotated {
    override fun <T : Annotation> getAnnotations(
        annotation: KClass<T>,
        containerAnnotation: KClass<out Annotation>?
    ): List<XAnnotationBox<T>> {
        // if there is a container annotation and annotation is repeated, we'll get the container.
        if (containerAnnotation != null) {
            MoreElements
                .getAnnotationMirror(element, containerAnnotation.java)
                .orNull()
                ?.box(env, containerAnnotation.java)
                ?.let { containerBox ->
                    // found a container, return
                    return containerBox.getAsAnnotationBoxArray<T>("value").toList()
                }
        }
        // if there is no container annotation or annotation is not repeated, we'll see the
        // individual value
        return MoreElements
            .getAnnotationMirror(element, annotation.java)
            .orNull()
            ?.box(env, annotation.java)
            ?.let {
                listOf(it)
            } ?: emptyList()
    }

    override fun hasAnnotation(
        annotation: KClass<out Annotation>,
        containerAnnotation: KClass<out Annotation>?
    ): Boolean {
        return isAnnotationPresent(element, annotation.java) ||
            (containerAnnotation != null && isAnnotationPresent(element, containerAnnotation.java))
    }

    override fun toString(): String {
        return element.toString()
    }

    override fun equals(other: Any?): Boolean {
        return XEquality.equals(this, other)
    }

    override fun hashCode(): Int {
        return XEquality.hashCode(equalityItems)
    }

    override fun kindName(): String {
        return element.kind.name.lowercase(Locale.US)
    }

    override fun hasAnnotationWithPackage(pkg: String): Boolean {
        return element.annotationMirrors.any {
            MoreElements.getPackage(it.annotationType.asElement()).toString() == pkg
        }
    }

    override val docComment: String? by lazy {
        env.elementUtils.getDocComment(element)
    }
}