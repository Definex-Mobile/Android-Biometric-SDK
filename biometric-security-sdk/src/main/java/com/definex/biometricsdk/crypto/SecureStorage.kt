package com.definex.biometricsdk.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.definex.biometricsdk.util.Logger

/**
 * Provides secure storage using EncryptedSharedPreferences.
 * All data is encrypted at rest using AES-256-GCM.
 */
class SecureStorage(context: Context) {
    
    private val sharedPreferences: SharedPreferences
    
    init {
        sharedPreferences = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            EncryptedSharedPreferences.create(
                context,
                "BiometricSDK_SecurePrefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Logger.e("Error creating EncryptedSharedPreferences", e)
            throw CryptoException("Failed to create secure storage", e)
        }
    }
    
    /**
     * Stores an encrypted string value.
     * 
     * @param key The key to store the value under
     * @param value The string value to encrypt and store
     */
    fun putEncryptedString(key: String, value: String) {
        try {
            sharedPreferences.edit()
                .putString(key, value)
                .apply()
            Logger.d("Stored encrypted string for key: $key")
        } catch (e: Exception) {
            Logger.e("Error storing encrypted string", e)
            throw CryptoException("Failed to store encrypted string", e)
        }
    }
    
    /**
     * Retrieves and decrypts a string value.
     * 
     * @param key The key to retrieve the value for
     * @return The decrypted string value, or null if not found
     */
    fun getEncryptedString(key: String): String? {
        return try {
            val value = sharedPreferences.getString(key, null)
            if (value != null) {
                Logger.d("Retrieved encrypted string for key: $key")
            }
            value
        } catch (e: Exception) {
            Logger.e("Error retrieving encrypted string", e)
            null
        }
    }
    
    /**
     * Stores an encrypted integer value.
     * 
     * @param key The key to store the value under
     * @param value The integer value to encrypt and store
     */
    fun putEncryptedInt(key: String, value: Int) {
        try {
            sharedPreferences.edit()
                .putInt(key, value)
                .apply()
            Logger.d("Stored encrypted int for key: $key")
        } catch (e: Exception) {
            Logger.e("Error storing encrypted int", e)
            throw CryptoException("Failed to store encrypted int", e)
        }
    }
    
    /**
     * Retrieves and decrypts an integer value.
     * 
     * @param key The key to retrieve the value for
     * @param defaultValue The default value to return if not found
     * @return The decrypted integer value
     */
    fun getEncryptedInt(key: String, defaultValue: Int = 0): Int {
        return try {
            sharedPreferences.getInt(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Error retrieving encrypted int", e)
            defaultValue
        }
    }
    
    /**
     * Stores an encrypted boolean value.
     * 
     * @param key The key to store the value under
     * @param value The boolean value to encrypt and store
     */
    fun putEncryptedBoolean(key: String, value: Boolean) {
        try {
            sharedPreferences.edit()
                .putBoolean(key, value)
                .apply()
            Logger.d("Stored encrypted boolean for key: $key")
        } catch (e: Exception) {
            Logger.e("Error storing encrypted boolean", e)
            throw CryptoException("Failed to store encrypted boolean", e)
        }
    }
    
    /**
     * Retrieves and decrypts a boolean value.
     * 
     * @param key The key to retrieve the value for
     * @param defaultValue The default value to return if not found
     * @return The decrypted boolean value
     */
    fun getEncryptedBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return try {
            sharedPreferences.getBoolean(key, defaultValue)
        } catch (e: Exception) {
            Logger.e("Error retrieving encrypted boolean", e)
            defaultValue
        }
    }
    
    /**
     * Removes a value from secure storage.
     * 
     * @param key The key to remove
     */
    fun remove(key: String) {
        try {
            sharedPreferences.edit()
                .remove(key)
                .apply()
            Logger.d("Removed key: $key")
        } catch (e: Exception) {
            Logger.e("Error removing key", e)
        }
    }
    
    /**
     * Checks if a key exists in secure storage.
     * 
     * @param key The key to check
     * @return true if the key exists
     */
    fun contains(key: String): Boolean {
        return try {
            sharedPreferences.contains(key)
        } catch (e: Exception) {
            Logger.e("Error checking key existence", e)
            false
        }
    }
    
    /**
     * Clears all data from secure storage.
     */
    fun clear() {
        try {
            sharedPreferences.edit()
                .clear()
                .apply()
            Logger.d("Cleared all secure storage")
        } catch (e: Exception) {
            Logger.e("Error clearing secure storage", e)
        }
    }
}

