package com.zeoflow.memo.compiler.processing

import com.zeoflow.memo.compiler.processing.javac.JavacProcessingEnv
import com.zeoflow.memo.compiler.processing.javac.JavacRoundEnv
import com.zeoflow.memo.compiler.processing.ksp.KspProcessingEnv
import com.zeoflow.memo.compiler.processing.ksp.KspRoundEnv
import javax.annotation.processing.RoundEnvironment
import kotlin.reflect.KClass

/**
 * Representation of an annotation processing round.
 *
 * @see javax.annotation.processing.RoundEnvironment
 */
interface XRoundEnv {
    /**
     * The root elements in the round.
     */
    val rootElements: Set<XElement>

    /**
     * Returns the set of [XElement]s that are annotated with the given annotation [klass].
     */
    fun getElementsAnnotatedWith(klass: KClass<out Annotation>): Set<XElement>

    fun getElementsAnnotatedWith(annotationQualifiedName: String): Set<XElement>

    companion object {
        /**
         * Creates an [XRoundEnv] from the given Java processing parameters.
         */
        @JvmStatic
        fun create(
            processingEnv: XProcessingEnv,
            roundEnvironment: RoundEnvironment? = null
        ): XRoundEnv {
            return when (processingEnv) {
                is JavacProcessingEnv -> {
                    checkNotNull(roundEnvironment)
                    JavacRoundEnv(processingEnv, roundEnvironment)
                }
                is KspProcessingEnv -> {
                    KspRoundEnv(processingEnv)
                }
                else -> error("invalid processing environment type: $processingEnv")
            }
        }
    }
}
