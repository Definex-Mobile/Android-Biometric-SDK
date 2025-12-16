package com.definex.biometricsdk.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.definex.biometricsdk.util.Logger
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Manages cryptographic keys in the Android KeyStore.
 * Generates hardware-backed keys that require biometric authentication.
 */
internal object KeyStoreManager {
    
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val DEFAULT_KEY_ALIAS = "BiometricSDK_Key"
    
    /**
     * Gets or creates a key for biometric-bound encryption.
     * The key requires biometric authentication for each use.
     * 
     * @param keyAlias The alias for the key in the KeyStore
     * @param invalidateOnEnrollment If true, key is invalidated when biometric enrollment changes
     * @return The SecretKey from the KeyStore
     */
    fun getOrCreateKey(
        keyAlias: String = DEFAULT_KEY_ALIAS,
        invalidateOnEnrollment: Boolean = true
    ): SecretKey {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            // Check if key already exists
            if (keyStore.containsAlias(keyAlias)) {
                val key = keyStore.getKey(keyAlias, null) as? SecretKey
                if (key != null) {
                    Logger.d("Retrieved existing key from KeyStore")
                    return key
                }
            }
            
            // Generate new key
            Logger.d("Generating new key in KeyStore")
            generateKey(keyAlias, invalidateOnEnrollment)
        } catch (e: Exception) {
            Logger.e("Error getting or creating key", e)
            throw CryptoException("Failed to get or create key", e)
        }
    }
    
    /**
     * Generates a new AES key in the KeyStore.
     * The key is hardware-backed and requires biometric authentication.
     */
    private fun generateKey(
        keyAlias: String,
        invalidateOnEnrollment: Boolean
    ): SecretKey {
        return try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            
            val builder = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(true)
                // -1 means authentication is required for each use
                .setUserAuthenticationParameters(
                    0, // timeout in seconds (0 = auth required per use)
                    KeyProperties.AUTH_BIOMETRIC_STRONG
                )
                .setInvalidatedByBiometricEnrollment(invalidateOnEnrollment)
            
            keyGenerator.init(builder.build())
            val key = keyGenerator.generateKey()
            
            Logger.d("Successfully generated new key in KeyStore")
            key
        } catch (e: Exception) {
            Logger.e("Error generating key", e)
            throw CryptoException("Failed to generate key", e)
        }
    }
    
    /**
     * Gets an existing key from the KeyStore.
     * 
     * @param keyAlias The alias of the key to retrieve
     * @return The SecretKey, or null if not found
     */
    fun getKey(keyAlias: String = DEFAULT_KEY_ALIAS): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (keyStore.containsAlias(keyAlias)) {
                keyStore.getKey(keyAlias, null) as? SecretKey
            } else {
                null
            }
        } catch (e: Exception) {
            Logger.e("Error getting key", e)
            null
        }
    }
    
    /**
     * Deletes a key from the KeyStore.
     * 
     * @param keyAlias The alias of the key to delete
     */
    fun deleteKey(keyAlias: String = DEFAULT_KEY_ALIAS) {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (keyStore.containsAlias(keyAlias)) {
                keyStore.deleteEntry(keyAlias)
                Logger.d("Deleted key from KeyStore")
            }
        } catch (e: Exception) {
            Logger.e("Error deleting key", e)
        }
    }
    
    /**
     * Checks if a key exists in the KeyStore.
     * 
     * @param keyAlias The alias of the key to check
     * @return true if the key exists
     */
    fun keyExists(keyAlias: String = DEFAULT_KEY_ALIAS): Boolean {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            keyStore.containsAlias(keyAlias)
        } catch (e: Exception) {
            Logger.e("Error checking key existence", e)
            false
        }
    }
}

/**
 * Exception thrown when cryptographic operations fail.
 */
class CryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)

