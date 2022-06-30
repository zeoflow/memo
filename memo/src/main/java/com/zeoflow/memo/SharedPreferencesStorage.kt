package com.zeoflow.memo

import android.content.Context
import android.content.SharedPreferences

internal class SharedPreferencesStorage : IStorage {
    private val preferences: SharedPreferences

    constructor(context: Context, tag: String?) {
        preferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE)
    }

    constructor(preferences: SharedPreferences) {
        this.preferences = preferences
    }

    override fun <T> put(key: String?, value: T): Boolean {
        MemoUtils.checkNull("key", key)
        return editor.putString(key, value.toString()).commit()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String?): T? {
        return preferences.getString(key, null) as T?
    }

    override fun delete(key: String?): Boolean {
        return editor.remove(key).commit()
    }

    override fun contains(key: String?): Boolean {
        return preferences.contains(key)
    }

    override fun deleteAll(): Boolean {
        return editor.clear().commit()
    }

    override fun count(): Long {
        return preferences.all.size.toLong()
    }

    private val editor: SharedPreferences.Editor
        get() = preferences.edit()
}