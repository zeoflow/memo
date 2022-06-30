package com.zeoflow.memo.processor.poet

import com.squareup.javapoet.ClassName

object MemoTypeNames {
    val Memo: ClassName = ClassName.get(
        "com.zeoflow.memo",
        "Memo"
    )
    val ConcealEncryption: ClassName = ClassName.get(
        "com.zeoflow.memo",
        "ConcealEncryption"
    )
    val NoEncryption: ClassName = ClassName.get(
        "com.zeoflow.memo",
        "NoEncryption"
    )
}

fun getCustomType(packageName: String, simpleName: String): ClassName {
    return ClassName.get(
        packageName,
        simpleName
    )
}