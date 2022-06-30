package com.zeoflow.memo.compiler.processing.ksp

import com.zeoflow.memo.compiler.processing.XElement
import com.zeoflow.memo.compiler.processing.XMessager
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.NonExistLocation
import javax.tools.Diagnostic

internal class KspMessager(
    private val logger: KSPLogger
) : XMessager() {
    override fun onPrintMessage(kind: Diagnostic.Kind, msg: String, element: XElement?) {
        val ksNode = (element as? KspElement)?.declaration

        @Suppress("NAME_SHADOWING") // intentional to avoid reporting without location
        val msg = if ((ksNode == null || ksNode.location == NonExistLocation) && element != null) {
            "$msg - ${element.fallbackLocationText}"
        } else {
            msg
        }
        when (kind) {
            Diagnostic.Kind.ERROR -> logger.error(msg, ksNode)
            Diagnostic.Kind.WARNING -> logger.warn(msg, ksNode)
            else -> logger.info(msg, ksNode)
        }
    }
}
