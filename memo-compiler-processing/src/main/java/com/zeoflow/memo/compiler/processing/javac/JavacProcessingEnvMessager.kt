package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XElement
import com.zeoflow.memo.compiler.processing.XMessager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

internal class JavacProcessingEnvMessager(
    private val processingEnv: ProcessingEnvironment
) : XMessager() {
    override fun onPrintMessage(kind: Diagnostic.Kind, msg: String, element: XElement?) {
        val javacElement = (element as? JavacElement)?.element
        processingEnv.messager.printMessage(
            kind,
            if (javacElement != null && javacElement.isFromCompiledClass()) {
                "$msg - ${element.fallbackLocationText}"
            } else {
                msg
            },
            javacElement
        )
    }

    companion object {
        /**
         * Indicates whether an element comes from a compiled class.
         *
         * If this method fails to identify if the element comes from a compiled class it will
         * default to returning false. Note that this is a poor-man's method of identifying if
         * the java source of the element is available without depending on compiler tools.
         */
        private fun Element.isFromCompiledClass(): Boolean {
            fun getClassFileString(symbol: Any): String =
                try {
                    symbol.javaClass.getDeclaredField("classfile").get(symbol).toString()
                } catch (ex: NoSuchFieldException) {
                    getClassFileString(
                        symbol.javaClass.superclass.getDeclaredField("owner").get(symbol)
                    )
                }

            return try {
                getClassFileString(this).let {
                    it.contains(".jar") || it.contains(".class")
                }
            } catch (ex: Throwable) {
                false
            }
        }
    }
}