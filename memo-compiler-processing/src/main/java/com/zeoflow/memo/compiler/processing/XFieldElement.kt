package com.zeoflow.memo.compiler.processing

/**
 * Field in an [XTypeElement].
 */
interface XFieldElement : XVariableElement, XHasModifiers {
    /**
     * The element that declared this field.
     * For fields declared in classes, this will be an [XTypeElement].
     *
     * For fields declared as top level properties in Kotlin:
     *   * When running with KAPT, the value will be an [XTypeElement].
     *   * When running with KSP, if this property is coming from the classpath, the value will
     *   be an [XTypeElement].
     *   * When running with KSP, if this property is in source, the value will **NOT** be an
     *   [XTypeElement]. If you need the generated synthetic java class name, you can use
     *   [XMemberContainer.className] property.
     */
    val enclosingElement: XMemberContainer

    override val fallbackLocationText: String
        get() = "$name in ${enclosingElement.fallbackLocationText}"
}
