package com.zeoflow.memo;

import android.content.Context;
import android.util.Base64;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.KeyChain;

import static com.zeoflow.memo.MemoApplication.getContext;

public class ConcealEncryption implements Encryption
{

    private final Crypto crypto;
    private final String encryptionKey;

    public ConcealEncryption(String encryptionKey)
    {
        this(getContext(), encryptionKey);
    }

    public ConcealEncryption(Context context, String encryptionKey)
    {
        this(new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256), encryptionKey);
    }

    protected ConcealEncryption(KeyChain keyChain, String encryptionKey)
    {
        this(AndroidConceal.get().createDefaultCrypto(keyChain), encryptionKey);
    }

    protected ConcealEncryption(Crypto crypto, String encryptionKey)
    {
        this.crypto = crypto;
        this.encryptionKey = encryptionKey;
    }

    @Override
    public String encryptionKey()
    {
        return encryptionKey;
    }
    @Override
    public boolean init()
    {
        return crypto.isAvailable();
    }

    @Override
    public String encrypt(String key, String plainText) throws Exception
    {
        Entity entity = Entity.create(key);
        byte[] bytes = crypto.encrypt(plainText.getBytes(), entity);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    @Override
    public String decrypt(String key, String cipherText) throws Exception
    {
        Entity entity = Entity.create(key);
        byte[] decodedBytes = Base64.decode(cipherText, Base64.NO_WRAP);
        byte[] bytes = crypto.decrypt(decodedBytes, entity);
        return new String(bytes);
    }

}
