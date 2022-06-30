package com.zeoflow.memo

interface IMemoFacade {
    fun <T> put(key: String, value: T?): Boolean
    operator fun <T> get(key: String?): T?
    operator fun <T> get(key: String?, defaultValue: T): T?
    fun <T> encrypt(value: T): String?
    fun <T> decrypt(value: String): T?
    fun count(): Long
    fun deleteAll(): Boolean
    fun delete(key: String?): Boolean
    operator fun contains(key: String?): Boolean
    fun isBuilt(): Boolean
    fun destroy()

    class EmptyMemoFacade : IMemoFacade {
        override fun <T> put(key: String, value: T?): Boolean {
            throwValidation()
            return false
        }

        override fun <T> get(key: String?): T? {
            throwValidation()
            return null
        }

        override fun <T> get(key: String?, defaultValue: T): T? {
            throwValidation()
            return null
        }

        override fun <T> encrypt(value: T): String? {
            throwValidation()
            return null
        }

        override fun <T> decrypt(value: String): T? {
            throwValidation()
            return null
        }

        override fun count(): Long {
            throwValidation()
            return 0
        }

        override fun deleteAll(): Boolean {
            throwValidation()
            return false
        }

        override fun delete(key: String?): Boolean {
            throwValidation()
            return false
        }

        override fun contains(key: String?): Boolean {
            throwValidation()
            return false
        }

        override fun isBuilt(): Boolean {
            return false
        }

        override fun destroy() {
            throwValidation()
        }

        private fun throwValidation() {
            throw IllegalStateException(
                "Memo is not built. " +
                        "Please call build() and wait until the initialisation finishes."
            )
        }
    }
}