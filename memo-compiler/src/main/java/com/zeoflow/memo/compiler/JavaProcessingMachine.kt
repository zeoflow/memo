package com.zeoflow.memo.compiler

import com.squareup.javapoet.ClassName
import com.zeoflow.memo.compiler.processing.XProcessingEnv
import com.zeoflow.memo.compiler.processing.XTypeElement
import com.zeoflow.memo.processor.ClassTypes
import com.zeoflow.memo.processor.Language
import com.zeoflow.memo.processor.ProcessingMachine

class JavaProcessingMachine(processingEnv: XProcessingEnv) : ProcessingMachine(processingEnv) {

    override fun process(functions: List<XTypeElement>) {

    }

    override fun language(): Language {
        return Language.JAVA
    }

    override fun types(): ClassTypes {
        return JavaClassTypes()
    }

}

class JavaClassTypes : ClassTypes() {

    override fun Memo(): Any {
        return ClassName.get(
            "com.zeoflow.memo",
            "Memo"
        )
    }

    override fun ConcealEncryption(): Any {
        return ClassName.get(
            "com.zeoflow.memo",
            "ConcealEncryption"
        )
    }

    override fun NoEncryption(): Any {
        return ClassName.get(
            "com.zeoflow.memo",
            "NoEncryption"
        )
    }

    override fun MutableLiveData(): Any {
        return ClassName.get(
            "androidx.lifecycle",
            "MutableLiveData"
        )
    }

    override fun Observer(): Any {
        return ClassName.get(
            "androidx.lifecycle",
            "Observer"
        )
    }

    override fun LifecycleOwner(): Any {
        return ClassName.get(
            "androidx.lifecycle",
            "LifecycleOwner"
        )
    }

}