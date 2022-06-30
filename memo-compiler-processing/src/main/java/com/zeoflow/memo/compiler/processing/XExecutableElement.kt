package com.zeoflow.memo.compiler.processing

/**
 * Represents a method, constructor or initializer.
 *
 * @see [javax.lang.model.element.ExecutableElement]
 */
interface XExecutableElement : XHasModifiers, XElement {
    /**
     * The element that declared this executable.
     *
     * For methods declared as top level functions in Kotlin:
     *   * When running with KAPT, the value will be an [XTypeElement].
     *   * When running with KSP, if this function is coming from the classpath, the value will
     *   be an [XTypeElement].
     *   * When running with KSP, if this function is in source, the value will **NOT** be an
     *   [XTypeElement]. If you need the generated synthetic java class name, you can use
     *   [XMemberContainer.className] property.
     */
    val enclosingElement: XMemberContainer
    /**
     * The list of parameters that should be passed into this method.
     *
     * @see [isVarArgs]
     */
    val parameters: List<XExecutableParameterElement>
    /**
     * Returns true if this method receives a vararg parameter.
     */
    fun isVarArgs(): Boolean
}
