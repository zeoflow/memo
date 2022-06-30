package com.zeoflow.memo.processor.poet

import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

object MemoTypeNamesK {
    val Memo: ClassName = ClassName(
        "com.zeoflow.memo",
        "Memo"
    )
    val ConcealEncryption: ClassName = ClassName(
        "com.zeoflow.memo",
        "ConcealEncryption"
    )
    val NoEncryption: ClassName = ClassName(
        "com.zeoflow.memo",
        "NoEncryption"
    )
}

fun getCustomTypeK(packageName: String, simpleName: String): ClassName {
    return ClassName(
        packageName,
        simpleName
    )
}

fun getType(typeName: TypeName): ClassName {
    if (typeName === TypeName.VOID) return ClassName("kotlin", "Void")
    if (typeName === TypeName.BOOLEAN) return ClassName("kotlin", "Boolean")
    if (typeName === TypeName.BYTE) return ClassName("kotlin", "Byte");
    if (typeName === TypeName.SHORT) return ClassName("kotlin", "Short")
    if (typeName === TypeName.INT) return ClassName("kotlin", "Int");
    if (typeName === TypeName.LONG) return ClassName("kotlin", "Long");
    if (typeName === TypeName.CHAR) return ClassName("kotlin", "Char");
    if (typeName === TypeName.FLOAT) return ClassName("kotlin", "Float");
    if (typeName === TypeName.DOUBLE) return ClassName("kotlin", "Double");
    val typeString = typeName.toString()
    val packageName = if(typeString.substringBeforeLast(".") == "java.lang") {
        "kotlin"
    } else {
        typeString.substringBeforeLast(".")
    }
    val simpleName = typeString.substringAfterLast(".")

    return ClassName(
        packageName,
        simpleName
    )
}

fun getDefault(typeName: TypeName): CodeBlock {
    val codeBlock = CodeBlock.builder()
    if (typeName === TypeName.BOOLEAN) codeBlock.add("false")
    if (typeName === TypeName.BYTE) codeBlock.add("0")
    if (typeName === TypeName.SHORT) codeBlock.add("0")
    if (typeName === TypeName.INT) codeBlock.add("0")
    if (typeName === TypeName.LONG) codeBlock.add("0L")
    if (typeName === TypeName.CHAR) codeBlock.add("'0'")
    if (typeName === TypeName.FLOAT) codeBlock.add("0F")
    if (typeName === TypeName.DOUBLE) codeBlock.add("0.0")
    val typeString = typeName.toString()
    val packageName = if(typeString.substringBeforeLast(".") == "java.lang") {
        "kotlin"
    } else {
        typeString.substringBeforeLast(".")
    }
    when(typeString.substringAfterLast(".")) {
        "String" -> codeBlock.add("\"\"")
    }

    return codeBlock.build()
}