package com.zeoflow.memo

internal object MemoUtils {
    fun checkNull(message: String, value: Any?) {
        if (value == null) {
            throw NullPointerException("$message should not be null")
        }
    }

    fun checkNullOrEmpty(message: String, value: String?) {
        if (isEmpty(value)) {
            throw NullPointerException("$message should not be null or empty")
        }
    }

    fun isEmpty(text: String?): Boolean {
        return text == null || text.trim { it <= ' ' }.length == 0
    }
}