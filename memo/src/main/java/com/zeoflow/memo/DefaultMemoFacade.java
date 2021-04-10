package com.zeoflow.memo;

public class DefaultMemoFacade implements MemoFacade
{

    private final Storage storage;
    private final Converter converter;
    private final Encryption encryption;
    private final Serializer serializer;
    private final LogInterceptor logInterceptor;

    public DefaultMemoFacade(MemoBuilder builder)
    {
        encryption = builder.getEncryption();
        storage = builder.getStorage();
        converter = builder.getConverter();
        serializer = builder.getSerializer();
        logInterceptor = builder.getLogInterceptor();

        logInterceptor.onLog("Memo.init -> Encryption : " + encryption.getClass().getSimpleName());
    }

    @Override
    public <T> boolean put(String key, T value)
    {
        // Validate
        MemoUtils.checkNull("Key", key);
        log("Memo.put -> key: " + key + ", value: " + value);

        // If the value is null, delete it
        if (value == null)
        {
            log("Memo.put -> Value is null. Any existing value will be deleted with the given key");
            return delete(key);
        }

        // 1. Convert to text
        String plainText = converter.toString(value);
        log("Memo.put -> Converted to " + plainText);
        if (plainText == null)
        {
            log("Memo.put -> Converter failed");
            return false;
        }

        // 2. Encrypt the text
        String cipherText = null;
        try
        {
            cipherText = encryption.encrypt(encryption.encryptionKey(), plainText);
            log("Memo.put -> Encrypted to " + cipherText);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (cipherText == null)
        {
            log("Memo.put -> Encryption failed");
            return false;
        }

        // 3. Serialize the given object along with the cipher text
        String serializedText = serializer.serialize(cipherText, value);
        log("Memo.put -> Serialized to " + serializedText);
        if (serializedText == null)
        {
            log("Memo.put -> Serialization failed");
            return false;
        }

        // 4. Save to the storage
        if (storage.put(key, serializedText))
        {
            log("Memo.put -> Stored successfully");
            return true;
        } else
        {
            log("Memo.put -> Store operation failed");
            return false;
        }
    }

    @Override
    public <T> T get(String key)
    {
        log("Memo.get -> key: " + key);
        if (key == null)
        {
            log("Memo.get -> null key, returning null value ");
            return null;
        }

        // 1. Get serialized text from the storage
        String serializedText = storage.get(key);
        log("Memo.get -> Fetched from storage : " + serializedText);
        if (serializedText == null)
        {
            log("Memo.get -> Fetching from storage failed");
            return null;
        }

        // 2. Deserialize
        DataInfo dataInfo = serializer.deserialize(serializedText);
        log("Memo.get -> Deserialized");
        if (dataInfo == null)
        {
            log("Memo.get -> Deserialization failed");
            return null;
        }

        // 3. Decrypt
        String plainText = null;
        try
        {
            plainText = encryption.decrypt(encryption.encryptionKey(), dataInfo.cipherText);
            log("Memo.get -> Decrypted to : " + plainText);
        } catch (Exception e)
        {
            log("Memo.get -> Decrypt failed: " + e.getMessage());
        }
        if (plainText == null)
        {
            log("Memo.get -> Decrypt failed");
            return null;
        }

        // 4. Convert the text to original data along with original type
        T result = null;
        try
        {
            result = converter.fromString(plainText, dataInfo);
            log("Memo.get -> Converted to : " + result);
        } catch (Exception e)
        {
            log("Memo.get -> Converter failed");
        }

        return result;
    }

    @Override
    public <T> T get(String key, T defaultValue)
    {
        T t = get(key);
        if (t == null) return defaultValue;
        return t;
    }

    @Override
    public <T> String encrypt(T value)
    {

        // 1. Convert to text
        String plainText = converter.toString(value);
        log("Memo.put -> Converted to " + plainText);
        if (plainText == null)
        {
            log("Memo.put -> Converter failed");
            return "";
        }

        // 2. Encrypt the text
        String cipherText = null;
        try
        {
            cipherText = encryption.encrypt("3wcbe78crg46wcbiafwcv6a3", plainText);
            log("Memo.put -> Encrypted to " + cipherText);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (cipherText == null)
        {
            log("Memo.put -> Encryption failed");
            return "";
        }

        // 3. Serialize the given object along with the cipher text
        String serializedText = serializer.serialize(cipherText, value);
        log("Memo.put -> Serialized to " + serializedText);
        if (serializedText == null)
        {
            log("Memo.put -> Serialization failed");
            return "";
        }
        return serializedText;
    }

    @Override
    public <T> T decrypt(String value)
    {

        // 1. Deserialize
        DataInfo dataInfo = serializer.deserialize(value);
        log("Memo.get -> Deserialized");
        if (dataInfo == null)
        {
            log("Memo.get -> Deserialization failed");
            return null;
        }

        // 2. Decrypt
        String plainText = null;
        try
        {
            plainText = encryption.decrypt("3wcbe78crg46wcbiafwcv6a3", dataInfo.cipherText);
            log("Memo.get -> Decrypted to : " + plainText);
        } catch (Exception e)
        {
            log("Memo.get -> Decrypt failed: " + e.getMessage());
        }
        if (plainText == null)
        {
            log("Memo.get -> Decrypt failed");
            return null;
        }

        // 3. Convert the text to original data along with original type
        T result = null;
        try
        {
            result = converter.fromString(plainText, dataInfo);
            log("Memo.get -> Converted to : " + result);
        } catch (Exception e)
        {
            log("Memo.get -> Converter failed");
        }

        return result;
    }

    @Override
    public long count()
    {
        return storage.count();
    }

    @Override
    public boolean deleteAll()
    {
        return storage.deleteAll();
    }

    @Override
    public boolean delete(String key)
    {
        return storage.delete(key);
    }

    @Override
    public boolean contains(String key)
    {
        return storage.contains(key);
    }

    @Override
    public boolean isBuilt()
    {
        return true;
    }

    @Override
    public void destroy()
    {
    }

    private void log(String message)
    {
        logInterceptor.onLog(message);
    }

}
