package com.zeoflow.memo.runtime

import android.content.Context
import com.zeoflow.startup.ktx.ApplicationInitializer

fun getContext() : Context {
    return ApplicationInitializer.context
}