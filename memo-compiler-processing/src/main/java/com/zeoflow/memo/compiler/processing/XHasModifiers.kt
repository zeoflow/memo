package com.zeoflow.memo.compiler.processing

/**
 * Common interface for elements which might have modifiers (e.g. field, method, class)
 */
interface XHasModifiers {
    /**
     * Returns `true` if this element is public (has public modifier in Java or not marked as
     * private / internal in Kotlin).
     */
    fun isPublic(): Boolean

    /**
     * Returns `true` if this element has protected modifier.
     */
    fun isProtected(): Boolean

    /**
     * Returns `true` if this element is declared as abstract.
     */
    fun isAbstract(): Boolean

    /**
     * Returns `true` if this element has private modifier.
     */
    fun isPrivate(): Boolean

    /**
     * Returns `true` if this element has static modifier.
     */
    fun isStatic(): Boolean

    /**
     * Returns `true` if this element has transient modifier.
     */
    fun isTransient(): Boolean

    /**
     * Returns `true` if this element is final and cannot be overridden.
     */
    fun isFinal(): Boolean
}
