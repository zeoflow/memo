package com.zeoflow.memo;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings({"unused", "RedundantSuppression", "RedundantThrows"})
public class ConcealEncryption implements Encryption
{

    private final String encryptionKey;
    private final SecretKey secretKey;

    public ConcealEncryption(String encryptionKey)
    {
        this(encryptionKey, true);
    }

    protected ConcealEncryption(String encryptionKey, boolean init)
    {
        this.encryptionKey = encryptionKey;
        int keyLength = 128;
        byte[] keyBytes = new byte[keyLength / 8];
        Arrays.fill(keyBytes, (byte) 0x0);
        byte[] passwordBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        int length = Math.min(passwordBytes.length, keyBytes.length);
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public String encryptionKey()
    {
        return encryptionKey;
    }
    @Override
    public boolean init()
    {
        return secretKey != null;
    }

    @SuppressLint("GetInstance")
    @Override
    public String encrypt(String key, String plainText) throws Exception
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(cipherText, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("GetInstance")
    @Override
    public String decrypt(String key, String cipherText) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP)), StandardCharsets.UTF_8);
    }

}
