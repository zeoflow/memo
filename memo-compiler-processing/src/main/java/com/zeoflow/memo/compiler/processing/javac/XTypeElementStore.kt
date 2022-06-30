package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XTypeElement
import java.lang.ref.WeakReference

/**
 * Utility class to cache type element wrappers.
 */
internal class XTypeElementStore<BackingType, T : XTypeElement>(
    private val findElement: (qName: String) -> BackingType?,
    private val getQName: (BackingType) -> String?,
    private val wrap: (type: BackingType) -> T
) {
    // instead of something like a Guava cache, we use a map of weak references here because our
    // main goal is avoiding to re-parse type elements as we go up & down in the hierarchy while
    // not necessarily wanting to preserve type elements after we are done with them. Doing that
    // could possibly hold a lot more information than we desire.
    private val typeCache = mutableMapOf<String, WeakReference<T>>()

    operator fun get(backingType: BackingType): T {
        val qName = getQName(backingType)
        @Suppress("FoldInitializerAndIfToElvis")
        if (qName == null) {
            // just wrap without caching, likely an error or local type in kotlin
            return wrap(backingType)
        }
        get(qName)?.let {
            return it
        }
        val wrapped = wrap(backingType)
        return cache(qName, wrapped)
    }

    operator fun get(qName: String): T? {
        typeCache[qName]?.get()?.let {
            return it
        }
        val result = findElement(qName)?.let(wrap) ?: return null
        return cache(qName, result)
    }

    private fun cache(qName: String, element: T): T {
        typeCache[qName] = WeakReference(element)
        return element
    }
}