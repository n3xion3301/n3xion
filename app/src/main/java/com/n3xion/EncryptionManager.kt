package com.n3xion

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class EncryptionManager {
    private val KEYSTORE = "AndroidKeyStore"
    private val KEY_ALIAS = "n3xion_master_key"
    private val ALGORITHM = "AES/GCM/NoPadding"
    private val TAG_LENGTH = 128
    
    init {
        createMasterKey()
    }
    
    private fun createMasterKey() {
        val keyStore = KeyStore.getInstance(KEYSTORE)
        keyStore.load(null)
        
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }
    
    private fun getMasterKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
    
    fun encrypt(plaintext: String): EncryptedData {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, getMasterKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray())
        
        return EncryptedData(
            ciphertext = Base64.encodeToString(ciphertext, Base64.NO_WRAP),
            iv = Base64.encodeToString(iv, Base64.NO_WRAP)
        )
    }
    
    fun decrypt(encryptedData: EncryptedData): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(
            TAG_LENGTH,
            Base64.decode(encryptedData.iv, Base64.NO_WRAP)
        )
        cipher.init(Cipher.DECRYPT_MODE, getMasterKey(), spec)
        
        val plaintext = cipher.doFinal(
            Base64.decode(encryptedData.ciphertext, Base64.NO_WRAP)
        )
        return String(plaintext)
    }
}

data class EncryptedData(
    val ciphertext: String,
    val iv: String
) {
    fun toSmsFormat(): String = "$iv:$ciphertext"
    
    companion object {
        fun fromSmsFormat(smsText: String): EncryptedData? {
            val parts = smsText.split(":", limit = 2)
            return if (parts.size == 2) {
                EncryptedData(ciphertext = parts[1], iv = parts[0])
            } else null
        }
    }
}
