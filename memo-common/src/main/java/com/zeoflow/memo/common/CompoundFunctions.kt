package com.zeoflow.memo.common

open class CompoundFunctions<T> {
    open operator fun get(value: T): T {
        return value
    }

    open fun set(value: T): T {
        return value
    }
}