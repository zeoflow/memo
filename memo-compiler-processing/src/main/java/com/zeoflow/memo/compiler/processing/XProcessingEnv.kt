package com.zeoflow.memo.compiler.processing

import com.zeoflow.memo.compiler.processing.javac.JavacProcessingEnv
import com.zeoflow.memo.compiler.processing.ksp.KspProcessingEnv
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.TypeName
import javax.annotation.processing.ProcessingEnvironment
import kotlin.reflect.KClass

/**
 * API for a Processor that is either backed by Java's Annotation Processing API or KSP.
 */
@ExperimentalProcessingApi
interface XProcessingEnv {

    val backend: Backend
    /**
     * The logger interface to log messages
     */
    val messager: XMessager

    /**
     * List of options passed into the annotation processor
     */
    val options: Map<String, String>

    /**
     * The API to generate files
     */
    val filer: XFiler

    /**
     * Looks for the [XTypeElement] with the given qualified name and returns `null` if it does not
     * exist.
     */
    fun findTypeElement(qName: String): XTypeElement?

    /**
     * Looks for the [XType] with the given qualified name and returns `null` if it does not exist.
     */
    fun findType(qName: String): XType?

    /**
     * Returns the [XType] with the given qualified name or throws an exception if it does not
     * exist.
     */
    fun requireType(qName: String): XType = checkNotNull(findType(qName)) {
        "cannot find required type $qName"
    }

    /**
     * Returns the [XTypeElement] for the annotation that should be added to the generated code.
     */
    fun findGeneratedAnnotation(): XTypeElement?

    /**
     * Returns an [XType] for the given [type] element with the type arguments specified
     * as in [types].
     */
    fun getDeclaredType(type: XTypeElement, vararg types: XType): XType

    /**
     * Return an [XArrayType] that has [type] as the [XArrayType.componentType].
     */
    fun getArrayType(type: XType): XArrayType

    /**
     * Returns the [XTypeElement] with the given qualified name or throws an exception if it does
     * not exist.
     */
    fun requireTypeElement(qName: String): XTypeElement {
        return checkNotNull(findTypeElement(qName)) {
            "Cannot find required type element $qName"
        }
    }

    // helpers for smooth migration, these could be extension methods
    fun requireType(typeName: TypeName) = checkNotNull(findType(typeName)) {
        "cannot find required type $typeName"
    }

    fun requireType(klass: KClass<*>) = requireType(klass.java.canonicalName!!)

    fun findType(typeName: TypeName): XType? {
        // TODO we probably need more complicated logic here but right now depot only has these
        //  usages.
        if (typeName is ArrayTypeName) {
            return findType(typeName.componentType)?.let {
                getArrayType(it)
            }
        }
        return findType(typeName.toString())
    }

    fun findType(klass: KClass<*>) = findType(klass.java.canonicalName!!)

    fun requireTypeElement(typeName: TypeName) = requireTypeElement(typeName.toString())

    fun requireTypeElement(klass: KClass<*>) = requireTypeElement(klass.java.canonicalName!!)

    fun findTypeElement(typeName: TypeName) = findTypeElement(typeName.toString())

    fun findTypeElement(klass: KClass<*>) = findTypeElement(klass.java.canonicalName!!)

    fun getArrayType(typeName: TypeName) = getArrayType(requireType(typeName))

    enum class Backend {
        JAVAC,
        KSP
    }

    companion object {
        /**
         * Creates a new [XProcessingEnv] implementation derived from the given Java [env].
         */
        @JvmStatic
        fun create(env: ProcessingEnvironment): XProcessingEnv = JavacProcessingEnv(env)

        /**
         * Creates a new [XProcessingEnv] implementation derived from the given KSP environment.
         */
        @JvmStatic
        fun create(
            options: Map<String, String>,
            resolver: Resolver,
            codeGenerator: CodeGenerator,
            logger: KSPLogger
        ): XProcessingEnv = KspProcessingEnv(
            options = options,
            codeGenerator = codeGenerator,
            logger = logger,
            resolver = resolver
        )
    }

    /**
     * Returns [XTypeElement]s with the given package name. Note that this call can be expensive.
     *
     * @param packageName the package name to look up.
     *
     * @return A list of [XTypeElement] with matching package name. This will return declarations
     * from both dependencies and source.
     */
    fun getTypeElementsFromPackage(packageName: String): List<XTypeElement>

    // TODO: Add support for getting top level members in a package
}
