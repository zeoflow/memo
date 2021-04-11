package com.zeoflow.memo;

import android.content.Context;

import static com.zeoflow.memo.MemoApplication.getContext;

/**
 * Secure, simple key-value storage for Android.
 */
public final class Memo
{

    static MemoFacade memoFacade = new MemoFacade.EmptyMemoFacade();

    private Memo()
    {
        // no instance
    }
    /**
     * This will init the memo without password protection.
     */
    public static MemoBuilder init()
    {
        return init(getContext());
    }
    /**
     * This will init the memo without password protection.
     *
     * @param context is used to instantiate context based objects.
     *                ApplicationContext will be used
     */
    public static MemoBuilder init(Context context)
    {
        MemoUtils.checkNull("Context", context);
        memoFacade = null;
        return new MemoBuilder(context);
    }

    static void build(MemoBuilder memoBuilder)
    {
        memoFacade = new DefaultMemoFacade(memoBuilder);
    }

    /**
     * Saves any type including any collection, primitive values or custom objects
     *
     * @param key   is required to differentiate the given data
     * @param value is the data that is going to be encrypted and persisted
     *
     * @return true if the operation is successful. Any failure in any step will return false
     */
    public static <T> boolean put(String key, T value)
    {
        return memoFacade.put(key, value);
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
    public static <T> T get(String key)
    {
        return memoFacade.get(key);
    }

    /**
     * Gets the saved data, if it is null, default value will be returned
     *
     * @param key          is used to get the saved data
     * @param defaultValue will be return if the response is null
     *
     * @return the saved object
     */
    public static <T> T get(String key, T defaultValue)
    {
        return memoFacade.get(key, defaultValue);
    }

    /**
     * Gets the encrypted value
     *
     * @param key is the data that should be encrypted
     *
     * @return the saved object
     */
    public static <T> String encrypt(T key)
    {
        return memoFacade.encrypt(key);
    }

    /**
     * Gets the decrypted value
     *
     * @param key is used to get the decrypted data
     *
     * @return the saved object
     */
    public static <T> T decrypt(String key)
    {
        return memoFacade.decrypt(key);
    }

    /**
     * Size of the saved data. Each key will be counted as 1
     *
     * @return the size
     */
    public static long count()
    {
        return memoFacade.count();
    }

    /**
     * Clears the storage, note that crypto data won't be deleted such as salt key etc.
     * Use resetCrypto in order to deleteAll crypto information
     *
     * @return true if deleteAll is successful
     */
    public static boolean deleteAll()
    {
        return memoFacade.deleteAll();
    }

    /**
     * Removes the given key/value from the storage
     *
     * @param key is used for removing related data from storage
     *
     * @return true if delete is successful
     */
    public static boolean delete(String key)
    {
        return memoFacade.delete(key);
    }

    /**
     * Checks the given key whether it exists or not
     *
     * @param key is the key to check
     *
     * @return true if it exists in the storage
     */
    public static boolean contains(String key)
    {
        return memoFacade.contains(key);
    }

    /**
     * Use this method to verify if Memo is ready to be used.
     *
     * @return true if correctly initialised and built. False otherwise.
     */
    public static boolean isBuilt()
    {
        return memoFacade.isBuilt();
    }

    public static void destroy()
    {
        memoFacade.destroy();
    }

}
