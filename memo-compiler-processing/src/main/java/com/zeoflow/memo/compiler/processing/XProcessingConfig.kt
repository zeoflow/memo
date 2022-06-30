package com.zeoflow.memo.compiler.processing

/**
 * Utility class to change some behavior in tests, like adding more strict tests.
 */
internal object XProcessingConfig {
    /**
     * When true, we do more strict checks and fail instead of workarounds or fallback
     * behaviors. Set to true in depot's own tests.
     */
    val STRICT_MODE by lazy {
        System.getProperty("$PROP_PREFIX.strict").toBoolean()
    }

    private const val PROP_PREFIX = "com.zeoflow.depot.compiler.processing"
}