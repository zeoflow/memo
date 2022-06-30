package com.zeoflow.memo

/**
 * Intermediate layer which stores the given data. Used by Memo.
 *
 *
 * Use custom implementation if the built-in implementations are not enough.
 *
 * @see SharedPreferencesStorage
 */
interface IStorage {
    /**
     * Put a single entry to storage
     *
     * @param key   the name of entry to put
     * @param value the value of entry
     * @param <T>   type of value of entry
     *
     * @return true if entry added successfully, otherwise false
    </T> */
    fun <T> put(key: String?, value: T): Boolean

    /**
     * Get single entry from storage
     *
     * @param key the name of entry to get
     * @param <T> type of value of entry
     *
     * @return the object related to given key
    </T> */
    operator fun <T> get(key: String?): T?

    /**
     * Remove single entry from storage
     *
     * @param key the name of entry to delete
     *
     * @return true if removal is successful, otherwise false
     */
    fun delete(key: String?): Boolean

    /**
     * Remove all entries in the storage
     *
     * @return true if clearance if successful, otherwise false
     */
    fun deleteAll(): Boolean

    /**
     * Retrieve count of entries in the storage
     *
     * @return entry count in the storage
     */
    fun count(): Long

    /**
     * Checks whether the storage contains an entry.
     *
     * @param key the name of entry to check
     *
     * @return true if the entry exists in the storage, otherwise false
     */
    operator fun contains(key: String?): Boolean
}