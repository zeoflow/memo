package com.zeoflow.memo.compiler.processing

import com.squareup.javapoet.ClassName

/**
 * Common interface for elements that can contain methods and properties.
 *
 * This is especially important for handling top level methods / properties in KSP where the
 * synthetic container class does not exist
 */
interface XMemberContainer : XElement {
    /**
     * The JVM ClassName for this container.
     * For top level members of a Kotlin file, you can use this [className] for code generation.
     */
    val className: ClassName

    /**
     * The [XType] for the container if this is an [XTypeElement] otherwise `null` if a type
     * representing this container does not exist (e.g. a top level Kotlin source file)
     */
    val type: XType?
}
