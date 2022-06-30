package com.zeoflow.memo.compiler.ktx

import com.squareup.kotlinpoet.ClassName
import com.zeoflow.memo.compiler.processing.XProcessingEnv
import com.zeoflow.memo.compiler.processing.XTypeElement
import com.zeoflow.memo.processor.ClassTypes
import com.zeoflow.memo.processor.Language
import com.zeoflow.memo.processor.ProcessingMachine

class KotlinProcessingMachine(processingEnv: XProcessingEnv) : ProcessingMachine(processingEnv) {

    override fun process(functions: List<XTypeElement>) {

    }

    override fun language(): Language {
        return Language.KOTLIN
    }

    override fun types(): ClassTypes {
        return JavaClassTypes()
    }

}

class JavaClassTypes : ClassTypes() {

    override fun Memo(): Any {
        return ClassName(
            "com.zeoflow.memo",
            "Memo"
        )
    }

    override fun ConcealEncryption(): Any {
        return ClassName(
            "com.zeoflow.memo",
            "ConcealEncryption"
        )
    }

    override fun NoEncryption(): Any {
        return ClassName(
            "com.zeoflow.memo",
            "NoEncryption"
        )
    }

    override fun MutableLiveData(): Any {
        return ClassName(
            "androidx.lifecycle",
            "MutableLiveData"
        )
    }

    override fun Observer(): Any {
        return ClassName(
            "androidx.lifecycle",
            "Observer"
        )
    }

    override fun LifecycleOwner(): Any {
        return ClassName(
            "androidx.lifecycle",
            "LifecycleOwner"
        )
    }

    override fun Inject(): Any {
        return ClassName(
            "javax.inject",
            "Inject"
        )
    }

}