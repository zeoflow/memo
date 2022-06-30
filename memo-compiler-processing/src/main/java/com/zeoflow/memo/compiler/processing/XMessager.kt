package com.zeoflow.memo.compiler.processing

import javax.tools.Diagnostic

/**
 * Logging interface for the processor
 */
abstract class XMessager {
    private val watchers = mutableListOf<XMessager>()
    /**
     * Prints the given [msg] to the logs while also associating it with the given [element].
     *
     * @param kind Kind of the message
     * @param msg The actual message to report to the compiler
     * @param element The element with whom the message should be associated with
     */
    fun printMessage(kind: Diagnostic.Kind, msg: String, element: XElement? = null) {
        watchers.forEach {
            it.printMessage(kind, msg, element)
        }
        onPrintMessage(kind, msg, element)
    }

    protected abstract fun onPrintMessage(
        kind: Diagnostic.Kind,
        msg: String,
        element: XElement? = null
    )

    fun addMessageWatcher(watcher: XMessager) {
        watchers.add(watcher)
    }

    fun removeMessageWatcher(watcher: XMessager) {
        watchers.remove(watcher)
    }
}
