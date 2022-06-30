package com.zeoflow.memo.compiler.processing.ksp

import androidx.annotation.VisibleForTesting
import com.zeoflow.memo.compiler.processing.XAnnotationBox
import com.zeoflow.memo.compiler.processing.XType

/**
 * KSP sometimes cannot read default values in annotations. This reflective implementation
 * handles those cases.
 * see: https://github.com/google/ksp/issues/53
 */
internal class KspReflectiveAnnotationBox<T : Annotation> @VisibleForTesting constructor(
    private val env: KspProcessingEnv,
    private val annotationClass: Class<T>,
    private val annotation: T
) : XAnnotationBox<T> {
    override val value: T = annotation

    override fun getAsType(methodName: String): XType? {
        val value = getFieldValue<Class<*>>(methodName) ?: return null
        return env.findType(value.kotlin)
    }

    override fun getAsTypeList(methodName: String): List<XType> {
        val values = getFieldValue<Array<*>>(methodName)
        return values?.filterIsInstance<Class<*>>()?.mapNotNull {
            env.findType(it.kotlin)
        } ?: emptyList()
    }

    override fun <T : Annotation> getAsAnnotationBox(methodName: String): XAnnotationBox<T> {
        return createFromDefaultValue(
            env = env,
            annotationClass = annotationClass,
            methodName = methodName
        )
    }

    @Suppress("UNCHECKED_CAST", "BanUncheckedReflection")
    override fun <T : Annotation> getAsAnnotationBoxArray(
        methodName: String
    ): Array<XAnnotationBox<T>> {
        val method = annotationClass.methods.firstOrNull {
            it.name == methodName
        } ?: error("$annotationClass does not contain $methodName")
        val values = method.invoke(annotation) as? Array<T> ?: return emptyArray()
        return values.map {
            KspReflectiveAnnotationBox(
                env = env,
                annotationClass = method.returnType.componentType as Class<T>,
                annotation = it
            )
        }.toTypedArray()
    }

    @Suppress("UNCHECKED_CAST", "BanUncheckedReflection")
    private fun <R : Any> getFieldValue(methodName: String): R? {
        val value = annotationClass.methods.firstOrNull {
            it.name == methodName
        }?.invoke(annotation) ?: return null
        return value as R?
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <R : Annotation> createFromDefaultValue(
            env: KspProcessingEnv,
            annotationClass: Class<*>,
            methodName: String
        ): KspReflectiveAnnotationBox<R> {
            val method = annotationClass.methods.firstOrNull {
                it.name == methodName
            } ?: error("$annotationClass does not contain $methodName")
            val defaultValue = method.defaultValue
                ?: error("$annotationClass.$method does not have a default value and is not set")
            return KspReflectiveAnnotationBox(
                env = env,
                annotationClass = method.returnType as Class<R>,
                annotation = defaultValue as R
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun <R : Annotation> createFromDefaultValues(
            env: KspProcessingEnv,
            annotationClass: Class<*>,
            methodName: String
        ): Array<XAnnotationBox<R>> {
            val method = annotationClass.methods.firstOrNull {
                it.name == methodName
            } ?: error("$annotationClass does not contain $methodName")
            check(method.returnType.isArray) {
                "expected ${method.returnType} to be an array. $method"
            }
            val defaultValue = method.defaultValue
                ?: error("$annotationClass.$method does not have a default value and is not set")
            val values: Array<R> = defaultValue as Array<R>
            return values.map {
                KspReflectiveAnnotationBox(
                    env = env,
                    annotationClass = method.returnType.componentType as Class<R>,
                    annotation = it
                )
            }.toTypedArray()
        }
    }
}