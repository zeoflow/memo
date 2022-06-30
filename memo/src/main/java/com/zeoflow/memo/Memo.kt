package com.zeoflow.memo

import android.content.Context
import com.zeoflow.startup.ktx.ApplicationInitializer

class Memo {

    companion object {

        var memoFacade: IMemoFacade = IMemoFacade.EmptyMemoFacade()
        /**
         * This will init the memo without password protection.
         *
         * @param context is used to instantiate context based objects.
         * ApplicationContext will be used
         */
        /**
         * This will init the memo without password protection.
         */
        @JvmOverloads
        fun init(context: Context = ApplicationInitializer.context): MemoBuilder {
            MemoUtils.checkNull("Context", context)
            return MemoBuilder(context)
        }

        fun build(memoBuilder: MemoBuilder) {
            memoFacade = DefaultMemoFacade(memoBuilder)
        }

        /**
         * Saves any type including any collection, primitive values or custom objects
         *
         * @param key   is required to differentiate the given data
         * @param value is the data that is going to be encrypted and persisted
         *
         * @return true if the operation is successful. Any failure in any step will return false
         */
        fun <T> put(key: String, value: T): Boolean {
            return memoFacade.put(key, value)
        }

        /**
         * Gets the original data along with original type by the given key.
         * This is not guaranteed operation since Memo uses serialization. Any change in in the requested
         * data type might affect the result. It's guaranteed to return primitive types and String type
         *
         * @param key is used to get the persisted data
         *
         * @return the original object
         */
        operator fun <T> get(key: String?): T {
            return memoFacade[key]!!
        }

        /**
         * Gets the saved data, if it is null, default value will be returned
         *
         * @param key          is used to get the saved data
         * @param defaultValue will be return if the response is null
         *
         * @return the saved object
         */
        operator fun <T> get(key: String?, defaultValue: T): T {
            return memoFacade[key, defaultValue]!!
        }

        /**
         * Gets the encrypted value
         *
         * @param key is the data that should be encrypted
         *
         * @return the saved object
         */
        fun <T> encrypt(key: T): String {
            return memoFacade.encrypt(key)!!
        }

        /**
         * Gets the decrypted value
         *
         * @param key is used to get the decrypted data
         *
         * @return the saved object
         */
        fun <T> decrypt(key: String): T {
            return memoFacade.decrypt(key)!!
        }

        /**
         * Size of the saved data. Each key will be counted as 1
         *
         * @return the size
         */
        fun count(): Long {
            return memoFacade.count()
        }

        /**
         * Clears the storage, note that crypto data won't be deleted such as salt key etc.
         * Use resetCrypto in order to deleteAll crypto information
         *
         * @return true if deleteAll is successful
         */
        fun deleteAll(): Boolean {
            return memoFacade.deleteAll()
        }

        /**
         * Removes the given key/value from the storage
         *
         * @param key is used for removing related data from storage
         *
         * @return true if delete is successful
         */
        fun delete(key: String?): Boolean {
            return memoFacade.delete(key)
        }

        /**
         * Checks the given key whether it exists or not
         *
         * @param key is the key to check
         *
         * @return true if it exists in the storage
         */
        operator fun contains(key: String?): Boolean {
            return memoFacade.contains(key)
        }

        /**
         * Use this method to verify if Memo is ready to be used.
         *
         * @return true if correctly initialised and built. False otherwise.
         */
        val isBuilt: Boolean
            get() = memoFacade.isBuilt()

        fun destroy() {
            memoFacade.destroy()
        }
    }
}