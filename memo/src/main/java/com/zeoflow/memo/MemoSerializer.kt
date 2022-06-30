package com.zeoflow.memo

internal class MemoSerializer(private val logInterceptor: ILogInterceptor?) : ISerializer {
    override fun <T> serialize(cipherText: String, originalGivenValue: T): String {
        MemoUtils.checkNullOrEmpty("Cipher text", cipherText)
        MemoUtils.checkNull("Value", originalGivenValue)
        var keyClassName = ""
        var valueClassName = ""
        val dataType: Char

        val givenValue = originalGivenValue!!::class.java

        if (MutableList::class.java.isAssignableFrom(originalGivenValue.javaClass)) {
            val list = originalGivenValue as List<*>
            if (list.isNotEmpty()) {
                keyClassName = list[0]!!.javaClass.name
            }
            dataType = DataInfo.Companion.TYPE_LIST
        } else if (MutableMap::class.java.isAssignableFrom(originalGivenValue.javaClass)) {
            dataType = DataInfo.Companion.TYPE_MAP
            val map = originalGivenValue as Map<*, *>
            if (map.isNotEmpty()) {
                for ((key, value) in map) {
                    keyClassName = key!!.javaClass.name
                    valueClassName = value!!.javaClass.name
                    break
                }
            }
        } else if (MutableSet::class.java.isAssignableFrom(originalGivenValue.javaClass)) {
            val set = originalGivenValue as Set<*>
            if (set.isNotEmpty()) {
                val iterator = set.iterator()
                if (iterator.hasNext()) {
                    keyClassName = iterator.next()!!.javaClass.name
                }
            }
            dataType = DataInfo.Companion.TYPE_SET
        } else {
            dataType = DataInfo.Companion.TYPE_OBJECT
            keyClassName = originalGivenValue.javaClass.name
        }
        return keyClassName + INFO_DELIMITER +
                valueClassName + INFO_DELIMITER +
                dataType + NEW_VERSION + DELIMITER +
                cipherText
    }

    override fun deserialize(serializedText: String): DataInfo {
        val infos = serializedText.split(INFO_DELIMITER).toTypedArray()
        val type = infos[2][0]

        // if it is collection, no need to create the class object
        var keyClazz: Class<*>? = null
        val firstElement = infos[0]
        if (firstElement.length != 0) {
            try {
                keyClazz = Class.forName(firstElement)
            } catch (e: ClassNotFoundException) {
                logInterceptor!!.onLog("MemoSerializer -> " + e.message)
            }
        }
        var valueClazz: Class<*>? = null
        val secondElement = infos[1]
        if (secondElement.length != 0) {
            try {
                valueClazz = Class.forName(secondElement)
            } catch (e: ClassNotFoundException) {
                logInterceptor!!.onLog("MemoSerializer -> " + e.message)
            }
        }
        val cipherText = getCipherText(infos[infos.size - 1])
        return DataInfo(type, cipherText, keyClazz, valueClazz)
    }

    private fun getCipherText(serializedText: String): String {
        val index = serializedText.indexOf(DELIMITER)
        require(index != -1) { "Text should contain delimiter" }
        return serializedText.substring(index + 1)
    }

    companion object {
        private const val DELIMITER = '@'
        private const val INFO_DELIMITER = "#"
        private const val NEW_VERSION = 'V'
    }
}