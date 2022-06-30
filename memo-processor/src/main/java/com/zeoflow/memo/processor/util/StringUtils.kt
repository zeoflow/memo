package com.zeoflow.memo.processor.util

import com.google.common.base.CaseFormat

object StringUtils {
    fun toUpperCamel(name: String?): String {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name.toString())
    }

    fun toLowerCamel(name: String?): String {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name.toString())
    }

    val errorMessagePrefix: String
        get() = "\n==================== <ERROR LOG> ====================\n"
}