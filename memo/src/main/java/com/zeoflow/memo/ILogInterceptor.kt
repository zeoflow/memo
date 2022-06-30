package com.zeoflow.memo

/**
 * Interceptor for all logs happens in the library
 */
interface ILogInterceptor {
    /**
     * Will be triggered each time when a log is written
     *
     * @param message is the log message
     */
    fun onLog(message: String?)
}