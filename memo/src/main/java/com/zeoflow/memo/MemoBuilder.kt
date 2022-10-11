package com.zeoflow.memo

import android.content.Context
import com.zeoflow.startup.ktx.ApplicationInitializer
import com.zeoflow.zson.Zson

class MemoBuilder @JvmOverloads constructor(context: Context = ApplicationInitializer.context) {
    private val context: Context

    init {
        MemoUtils.checkNull("Context", context)
        this.context = context.applicationContext
    }

    fun withLogInterceptor(logInterceptor: ILogInterceptor) : MemoBuilder {
        this.logInterceptor = logInterceptor
        return this
    }

    fun withSerializer(serializer: ISerializer) : MemoBuilder {
        this.serializer = serializer
        return this
    }

    fun withCryptoStorage(cryptoStorage: IStorage) : MemoBuilder {
        this.cryptoStorage = cryptoStorage
        return this
    }

    fun withParser(parser: IParser) : MemoBuilder {
        this.parser = parser
        return this
    }

    fun withConverter(converter: IConverter) : MemoBuilder {
        this.converter = converter
        return this
    }

    fun withEncryption(encryption: Encryption) : MemoBuilder {
        this.encryption = encryption
        return this
    }

    var logInterceptor: ILogInterceptor = object : ILogInterceptor {
        override fun onLog(message: String?) {

        }
    }
    var serializer: ISerializer = MemoSerializer(logInterceptor)
    var cryptoStorage: IStorage = SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE)
    var parser: IParser = ZsonParser(Zson())
    var converter: IConverter = MemoConverter(parser)
    val storage: IStorage
        get() {
            return cryptoStorage
        }
    var encryption: Encryption = ConcealEncryption("default")
        get() {
            if (field is ConcealEncryption) {
                if (!(field as ConcealEncryption).init()) {
                    encryption = NoEncryption()
                }
            }
            return field
        }

    fun build() {
        Memo.build(this)
    }

    companion object {
        /**
         * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
         * It will break backward compatibility in terms of keeping previous data
         */
        private const val STORAGE_TAG_DO_NOT_CHANGE = "MemoStorage"
    }
}