package com.zeoflow.memo

class DefaultMemoFacade(builder: MemoBuilder) : IMemoFacade {
    private val storage: IStorage?
    private val converter: IConverter?
    private val encryption: Encryption?
    private val serializer: ISerializer?
    private val logInterceptor: ILogInterceptor

    init {
        encryption = builder.encryption
        storage = builder.storage
        converter = builder.converter
        serializer = builder.serializer
        logInterceptor = builder.logInterceptor
        logInterceptor.onLog("Memo.init -> Encryption : " + encryption!!.javaClass.simpleName)
    }

    override fun <T> put(key: String, value: T?): Boolean {
        // Validate
        MemoUtils.checkNull("Key", key)
        log("Memo.put -> key: $key, value: $value")

        // If the value is null, delete it
        if (value == null) {
            log("Memo.put -> Value is null. Any existing value will be deleted with the given key")
            return delete(key)
        }

        // 1. Convert to text
        val plainText = converter!!.toString(value)
        log("Memo.put -> Converted to $plainText")
        if (plainText == null) {
            log("Memo.put -> IConverter failed")
            return false
        }

        // 2. Encrypt the text
        var cipherText: String? = null
        try {
            cipherText = encryption!!.encrypt(encryption.encryptionKey(), plainText)
            log("Memo.put -> Encrypted to $cipherText")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (cipherText == null) {
            log("Memo.put -> Encryption failed")
            return false
        }

        // 3. Serialize the given object along with the cipher text
        val serializedText = serializer!!.serialize(cipherText, value)
        log("Memo.put -> Serialized to $serializedText")
        if (serializedText == null) {
            log("Memo.put -> Serialization failed")
            return false
        }

        // 4. Save to the storage
        return if (storage!!.put(key, serializedText)) {
            log("Memo.put -> Stored successfully")
            true
        } else {
            log("Memo.put -> Store operation failed")
            false
        }
    }

    override fun <T> get(key: String?): T? {
        log("Memo.get -> key: $key")
        if (key == null) {
            log("Memo.get -> null key, returning null value ")
            return null
        }

        // 1. Get serialized text from the storage
        val serializedText = storage!!.get<String>(key)
        log("Memo.get -> Fetched from storage : $serializedText")
        if (serializedText == null) {
            log("Memo.get -> Fetching from storage failed")
            return null
        }

        // 2. Deserialize
        val dataInfo = serializer!!.deserialize(serializedText)
        log("Memo.get -> Deserialized")
        if (dataInfo == null) {
            log("Memo.get -> Deserialization failed")
            return null
        }

        // 3. Decrypt
        var plainText: String? = null
        try {
            plainText = encryption!!.decrypt(encryption.encryptionKey(), dataInfo.cipherText)
            log("Memo.get -> Decrypted to : $plainText")
        } catch (e: Exception) {
            log("Memo.get -> Decrypt failed: " + e.message)
        }
        if (plainText == null) {
            log("Memo.get -> Decrypt failed")
            return null
        }

        // 4. Convert the text to original data along with original type
        var result: T? = null
        try {
            result = converter!!.fromString(plainText, dataInfo)
            log("Memo.get -> Converted to : $result")
        } catch (e: Exception) {
            log("Memo.get -> IConverter failed")
        }
        return result
    }

    override fun <T> get(key: String?, defaultValue: T): T? {
        return get<T>(key) ?: return defaultValue
    }

    override fun <T> encrypt(value: T): String? {

        // 1. Convert to text
        val plainText = converter!!.toString(value)
        log("Memo.put -> Converted to $plainText")
        if (plainText == null) {
            log("Memo.put -> IConverter failed")
            return ""
        }

        // 2. Encrypt the text
        var cipherText: String? = null
        try {
            cipherText = encryption!!.encrypt("3wcbe78crg46wcbiafwcv6a3", plainText)
            log("Memo.put -> Encrypted to $cipherText")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (cipherText == null) {
            log("Memo.put -> Encryption failed")
            return ""
        }

        // 3. Serialize the given object along with the cipher text
        val serializedText = serializer!!.serialize(cipherText, value)
        log("Memo.put -> Serialized to $serializedText")
        if (serializedText == null) {
            log("Memo.put -> Serialization failed")
            return ""
        }
        return serializedText
    }

    override fun <T> decrypt(value: String): T? {

        // 1. Deserialize
        val dataInfo = serializer!!.deserialize(value)
        log("Memo.get -> Deserialized")
        if (dataInfo == null) {
            log("Memo.get -> Deserialization failed")
            return null
        }

        // 2. Decrypt
        var plainText: String? = null
        try {
            plainText = encryption!!.decrypt("3wcbe78crg46wcbiafwcv6a3", dataInfo.cipherText)
            log("Memo.get -> Decrypted to : $plainText")
        } catch (e: Exception) {
            log("Memo.get -> Decrypt failed: " + e.message)
        }
        if (plainText == null) {
            log("Memo.get -> Decrypt failed")
            return null
        }

        // 3. Convert the text to original data along with original type
        var result: T? = null
        try {
            result = converter!!.fromString(plainText, dataInfo)
            log("Memo.get -> Converted to : $result")
        } catch (e: Exception) {
            log("Memo.get -> IConverter failed")
        }
        return result
    }

    override fun count(): Long {
        return storage!!.count()
    }

    override fun deleteAll(): Boolean {
        return storage!!.deleteAll()
    }

    override fun delete(key: String?): Boolean {
        return storage!!.delete(key)
    }

    override fun contains(key: String?): Boolean {
        return storage!!.contains(key)
    }

    override fun isBuilt(): Boolean {
        return true
    }

    override fun destroy() {

    }

    private fun log(message: String) {
        logInterceptor.onLog(message)
    }
}