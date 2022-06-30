package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XFiler
import com.squareup.javapoet.JavaFile
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.ProcessingEnvironment

internal class JavacFiler(val processingEnv: ProcessingEnvironment) : XFiler {

    // "mode" is ignored in javac, and only applicable in KSP
    override fun write(javaFile: JavaFile, mode: XFiler.Mode) {
        javaFile.writeTo(processingEnv.filer)
    }

    override fun write(fileSpec: FileSpec, mode: XFiler.Mode) {
        require(processingEnv.options.containsKey("kapt.kotlin.generated")) {
            val filePath = fileSpec.packageName.replace('.', '/')
            "Could not generate kotlin file $filePath/${fileSpec.name}.kt. The " +
                "annotation processing environment is not set to generate Kotlin files."
        }
        fileSpec.writeTo(processingEnv.filer)
    }
}
