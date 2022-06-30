package com.zeoflow.memo.compiler.processing

import com.squareup.javapoet.JavaFile
import com.squareup.kotlinpoet.FileSpec

/**
 * Code generation interface for XProcessing.
 */
interface XFiler {

    fun write(javaFile: JavaFile, mode: Mode = Mode.Isolating)

    fun write(fileSpec: FileSpec, mode: Mode = Mode.Isolating)

    /**
     * Specifies whether a file represents aggregating or isolating inputs for incremental
     * build purposes. This does not apply in Javac processing because aggregating vs isolating
     * is set on the processor level. For more on KSP's definitions of isolating vs aggregating
     * see the documentation at
     * https://github.com/google/ksp/blob/master/docs/incremental.md
     */
    enum class Mode {
        Aggregating, Isolating
    }
}

fun JavaFile.writeTo(generator: XFiler, mode: XFiler.Mode = XFiler.Mode.Isolating) {
    generator.write(this, mode)
}

fun FileSpec.writeTo(generator: XFiler, mode: XFiler.Mode = XFiler.Mode.Isolating) {
    generator.write(this, mode)
}
