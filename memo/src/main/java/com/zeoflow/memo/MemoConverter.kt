package com.zeoflow.memo

import com.zeoflow.zson.reflect.TypeToken

/**
 * Concrete implementation of encoding and decoding.
 * List types will be encoded/decoded by parser
 * Serializable types will be encoded/decoded object stream
 * Not serializable objects will be encoded/decoded by parser
 */
internal class MemoConverter(parser: IParser?) : IConverter {
    private val parser: IParser

    init {
        if (parser == null) {
            throw NullPointerException("IParser should not be null")
        }
        this.parser = parser
    }

    override fun <T> toString(value: T?): String? {
        return if (value == null) {
            null
        } else parser.toJson(value)
    }

    @Throws(Exception::class)
    override fun <T> fromString(value: String?, info: DataInfo): T? {
        if (value == null) {
            return null
        }
        MemoUtils.checkNull("data info", info)
        val keyType = info.keyClazz
        val valueType = info.valueClazz
        return when (info.dataType) {
            DataInfo.Companion.TYPE_OBJECT -> toObject<T>(value, keyType)
            DataInfo.Companion.TYPE_LIST -> toList<T>(value, keyType)
            DataInfo.Companion.TYPE_MAP -> toMap<Any, Any, T>(value, keyType, valueType)
            DataInfo.Companion.TYPE_SET -> toSet<T>(value, keyType)
            else -> null
        }
    }

    @Throws(Exception::class)
    private fun <T> toObject(json: String, type: Class<*>?): T? {
        return parser.fromJson(json, type)
    }

    @Throws(Exception::class)
    private fun <T> toList(json: String, type: Class<*>?): T {
        if (type == null) {
            return ArrayList<Any>() as T
        }
        val list = parser.fromJson<MutableList<T?>>(
            json,
            object : TypeToken<List<T>?>() {}.type
        )!!
        val size = list.size
        for (i in 0 until size) {
            list[i] = parser.fromJson(parser.toJson(list[i]), type)
        }
        return list as T
    }

    @Throws(Exception::class)
    private fun <T> toSet(json: String, type: Class<*>?): T {
        val resultSet: MutableSet<T?> = HashSet()
        if (type == null) {
            return resultSet as T
        }
        val set = parser.fromJson<Set<T>>(json, object : TypeToken<Set<T>?>() {}.type)!!
        for (t in set) {
            val valueJson = parser.toJson(t)
            val value = parser.fromJson<T>(valueJson, type)
            resultSet.add(value)
        }
        return resultSet as T
    }

    @Throws(Exception::class)
    private fun <K, V, T> toMap(json: String, keyType: Class<*>?, valueType: Class<*>?): T {
        val resultMap: MutableMap<K?, V?> = HashMap()
        if (keyType == null || valueType == null) {
            return resultMap as T
        }
        val map = parser.fromJson<Map<K, V>>(json, object : TypeToken<Map<K, V>?>() {}.type)!!
        for ((key, value) in map) {
            val keyJson = parser.toJson(key)
            val k = parser.fromJson<K>(keyJson, keyType)
            val valueJson = parser.toJson(value)
            val v = parser.fromJson<V>(valueJson, valueType)
            resultMap[k] = v
        }
        return resultMap as T
    }
}