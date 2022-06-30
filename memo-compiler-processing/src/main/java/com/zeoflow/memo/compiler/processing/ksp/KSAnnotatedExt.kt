package com.zeoflow.memo.compiler.processing.ksp

import com.google.devtools.ksp.symbol.KSAnnotated

private fun KSAnnotated.hasAnnotationWithQName(qName: String) = annotations.any {
    it.annotationType.resolve().declaration.qualifiedName?.asString() == qName
}

internal fun KSAnnotated.hasJvmStaticAnnotation() = hasAnnotationWithQName("kotlin.jvm.JvmStatic")

internal fun KSAnnotated.hasJvmTransientAnnotation() =
    hasAnnotationWithQName("kotlin.jvm.Transient")

internal fun KSAnnotated.hasJvmFieldAnnotation() = hasAnnotationWithQName("kotlin.jvm.JvmField")

internal fun KSAnnotated.hasJvmDefaultAnnotation() = hasAnnotationWithQName("kotlin.jvm.JvmDefault")
