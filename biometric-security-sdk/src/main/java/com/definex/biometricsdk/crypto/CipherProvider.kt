package com.definex.biometricsdk.crypto

import com.definex.biometricsdk.model.EncryptedPayload
import com.definex.biometricsdk.util.Logger
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Provides cipher instances for encryption and decryption.
 * Uses AES-GCM for authenticated encryption.
 */
internal object CipherProvider {
    
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_IV_LENGTH = 12 // 96 bits
    private const val GCM_TAG_LENGTH = 128 // bits
    
    /**
     * Creates a cipher for encryption.
     * Generates a random IV for each encryption operation.
     * 
     * @param key The secret key to use for encryption
     * @return Pair of (Cipher, IV)
     */
    fun getEncryptCipher(key: SecretKey): Pair<Cipher, ByteArray> {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = ByteArray(GCM_IV_LENGTH)
            SecureRandom().nextBytes(iv)
            
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, spec)
            
            Logger.d("Created encryption cipher")
            Pair(cipher, iv)
        } catch (e: Exception) {
            Logger.e("Error creating encryption cipher", e)
            throw CryptoException("Failed to create encryption cipher", e)
        }
    }
    
    /**
     * Creates a cipher for decryption.
     * 
     * @param key The secret key to use for decryption
     * @param iv The initialization vector used during encryption
     * @return Cipher configured for decryption
     */
    fun getDecryptCipher(key: SecretKey, iv: ByteArray): Cipher {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            
            Logger.d("Created decryption cipher")
            cipher
        } catch (e: Exception) {
            Logger.e("Error creating decryption cipher", e)
            throw CryptoException("Failed to create decryption cipher", e)
        }
    }
    
    /**
     * Encrypts data using the provided key.
     * 
     * @param data The data to encrypt
     * @param key The secret key to use
     * @return EncryptedPayload containing IV and ciphertext
     */
    fun encrypt(data: ByteArray, key: SecretKey): EncryptedPayload {
        return try {
            val (cipher, iv) = getEncryptCipher(key)
            val ciphertext = cipher.doFinal(data)
            
            Logger.d("Successfully encrypted data")
            EncryptedPayload(iv, ciphertext)
        } catch (e: Exception) {
            Logger.e("Error encrypting data", e)
            throw CryptoException("Failed to encrypt data", e)
        }
    }
    
    /**
     * Decrypts data using the provided key and IV.
     * 
     * @param payload The encrypted payload containing IV and ciphertext
     * @param key The secret key to use
     * @return The decrypted data
     */
    fun decrypt(payload: EncryptedPayload, key: SecretKey): ByteArray {
        return try {
            val cipher = getDecryptCipher(key, payload.iv)
            val plaintext = cipher.doFinal(payload.ciphertext)
            
            Logger.d("Successfully decrypted data")
            plaintext
        } catch (e: Exception) {
            Logger.e("Error decrypting data", e)
            throw CryptoException("Failed to decrypt data", e)
        }
    }
}

