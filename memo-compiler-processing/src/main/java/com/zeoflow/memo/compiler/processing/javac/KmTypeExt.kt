package com.zeoflow.memo.compiler.processing.javac

import com.zeoflow.memo.compiler.processing.XNullability
import com.zeoflow.memo.compiler.processing.javac.kotlin.KmType

internal val KmType.nullability: XNullability
    get() = if (isNullable()) {
        com.zeoflow.memo.compiler.processing.XNullability.NULLABLE
    } else {
        // if there is an upper bound information, use its nullability (e.g. it might be T : Foo?)
        extendsBound?.nullability ?: com.zeoflow.memo.compiler.processing.XNullability.NONNULL
    }
