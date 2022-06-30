package com.zeoflow.memo

import android.text.TextUtils
import com.zeoflow.zson.JsonSyntaxException
import com.zeoflow.zson.Zson
import java.lang.reflect.Type

class ZsonParser(private val zson: Zson) : IParser {
    @Throws(JsonSyntaxException::class)
    override fun <T> fromJson(content: String?, type: Type?): T? {
        return if (TextUtils.isEmpty(content)) {
            null
        } else zson.fromJson(content, type)
    }

    override fun toJson(body: Any?): String? {
        return zson.toJson(body)
    }
}