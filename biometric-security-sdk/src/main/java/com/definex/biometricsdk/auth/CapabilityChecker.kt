package com.definex.biometricsdk.auth

import android.content.Context
import android.content.pm.PackageManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import com.definex.biometricsdk.model.BiometricType
import com.definex.biometricsdk.util.Logger

/**
 * Checks device capabilities for biometric authentication.
 * Detects which biometric sensors are available on the device.
 */
internal object CapabilityChecker {
    
    /**
     * Gets all available biometric types on the device.
     * 
     * @param context Android context
     * @return Set of available BiometricType
     */
    fun getAvailableBiometrics(context: Context): Set<BiometricType> {
        val availableBiometrics = mutableSetOf<BiometricType>()
        
        // First check if biometric authentication is available at all
        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG)
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Logger.d("No biometric authentication available: $canAuthenticate")
            return emptySet()
        }
        
        // Check for specific biometric types
        if (hasFingerprint(context)) {
            availableBiometrics.add(BiometricType.FINGERPRINT)
            Logger.d("Fingerprint sensor detected")
        }
        
        if (hasFace(context)) {
            availableBiometrics.add(BiometricType.FACE)
            Logger.d("Face sensor detected")
        }
        
        if (hasIris(context)) {
            availableBiometrics.add(BiometricType.IRIS)
            Logger.d("Iris sensor detected")
        }
        
        Logger.d("Available biometrics: $availableBiometrics")
        return availableBiometrics
    }
    
    /**
     * Checks if fingerprint sensor is available.
     */
    private fun hasFingerprint(context: Context): Boolean {
        return try {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        } catch (e: Exception) {
            Logger.w("Error checking fingerprint capability", e)
            false
        }
    }
    
    /**
     * Checks if face sensor is available.
     */
    private fun hasFace(context: Context): Boolean {
        return try {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
        } catch (e: Exception) {
            Logger.w("Error checking face capability", e)
            false
        }
    }
    
    /**
     * Checks if iris sensor is available.
     */
    private fun hasIris(context: Context): Boolean {
        return try {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_IRIS)
        } catch (e: Exception) {
            Logger.w("Error checking iris capability", e)
            false
        }
    }
    
    /**
     * Checks if a specific biometric type is available.
     * 
     * @param context Android context
     * @param type The biometric type to check
     * @return true if the biometric type is available
     */
    fun isBiometricAvailable(context: Context, type: BiometricType): Boolean {
        val availableBiometrics = getAvailableBiometrics(context)
        return availableBiometrics.contains(type)
    }
    
    /**
     * Checks if any biometric authentication is available.
     * 
     * @param context Android context
     * @return true if any biometric authentication is available
     */
    fun isAnyBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG)
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }
}

