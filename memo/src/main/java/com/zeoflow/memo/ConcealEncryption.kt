package com.zeoflow.memo

import android.annotation.SuppressLint
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

open class ConcealEncryption constructor(
    private val encryptionKey: String,
) : Encryption {

    private val secretKey: SecretKey?

    init {
        val keyLength = 128
        val keyBytes = ByteArray(keyLength / 8)
        Arrays.fill(keyBytes, 0x0.toByte())
        val passwordBytes = encryptionKey.toByteArray(StandardCharsets.UTF_8)
        val length = passwordBytes.size.coerceAtMost(keyBytes.size)
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length)
        secretKey = SecretKeySpec(keyBytes, "AES/GCM/NoPadding")
    }

    override fun encryptionKey(): String {
        return encryptionKey
    }

    override fun init(): Boolean {
        return secretKey != null
    }

    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    override fun encrypt(key: String?, value: String): String? {
        try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val cipherText = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
            return Base64.encodeToString(cipherText, Base64.NO_WRAP)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("GetInstance")
    @Throws(Exception::class)
    override fun decrypt(key: String?, value: String?): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return String(
            cipher.doFinal(Base64.decode(value, Base64.NO_WRAP)),
            StandardCharsets.UTF_8
        )
    }
}