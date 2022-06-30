package com.zeoflow.memo.processor

import com.zeoflow.memo.compiler.processing.XProcessingEnv
import com.zeoflow.memo.compiler.processing.XTypeElement

abstract class ProcessingMachine(var processingEnv: XProcessingEnv) {

    abstract fun process(functions: List<XTypeElement>)

    abstract fun language(): Language

    abstract fun types(): ClassTypes
}

abstract class ClassTypes {

    abstract fun Memo(): Any
    abstract fun ConcealEncryption(): Any
    abstract fun NoEncryption(): Any

    abstract fun MutableLiveData(): Any
    abstract fun Observer(): Any
    abstract fun LifecycleOwner(): Any

}

enum class Language {
    JAVA,
    KOTLIN
}