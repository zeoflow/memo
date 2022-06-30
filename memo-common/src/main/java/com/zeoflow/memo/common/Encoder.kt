package com.zeoflow.memo.common

import androidx.annotation.RequiresApi
import com.zeoflow.memo.common.MemoStorage
import java.nio.charset.StandardCharsets

public object Encoder {
    @JvmStatic
    fun encodeUtf8(string: String?): String {
        val byteBuffer = StandardCharsets.UTF_8.encode(string.toString())
        return StandardCharsets.UTF_8.decode(byteBuffer).toString()
    }
}