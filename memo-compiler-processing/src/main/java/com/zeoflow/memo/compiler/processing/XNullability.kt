package com.zeoflow.memo.compiler.processing

/**
 * Declares the nullability of a type or element.
 */
enum class XNullability {
    /**
     * The type is guaranteed to be nullable. This means it is either a Kotlin Type declared with a
     * `?` at the end or it is a Java type that has one of the `nullable` annotations (e.g.
     * [androidx.annotation.Nullable].
     */
    NULLABLE,
    /**
     * The type is guaranteed to be nonnull. This means it is either a Kotlin Type declared
     * without a `?` at the end or it is a Java type that has one of the `non-null` annotations
     * (e.g. [androidx.annotation.NonNull].
     */
    NONNULL,
    /**
     * The nullability of the type is unknown. This happens if this is a non-primitive Java type
     * that does not have a nullability annotation or a Type in Kotlin where it is inferred from
     * the platform.
     */
    UNKNOWN
}
