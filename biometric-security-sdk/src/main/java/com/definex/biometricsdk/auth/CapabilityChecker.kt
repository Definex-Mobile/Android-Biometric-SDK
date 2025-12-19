package com.definex.biometricsdk.auth

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import com.definex.biometricsdk.model.BiometricType
import com.definex.biometricsdk.util.Logger

/**
 * Checks device capabilities for biometric authentication.
 * Detects which biometric sensors are available AND enrolled on the device.
 */
internal object CapabilityChecker {
    
    /**
     * Gets all available AND enrolled biometric types on the device.
     * 
     * @param context Android context
     * @return Set of available and enrolled BiometricType
     */
    fun getAvailableBiometrics(context: Context): Set<BiometricType> {
        val availableBiometrics = mutableSetOf<BiometricType>()
        
        // First check if biometric authentication is available at all
        val biometricManager = BiometricManager.from(context)
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_WEAK)
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            Logger.d("No biometric authentication available or enrolled: $canAuthenticate")
            return emptySet()
        }
        
        // Check for specific biometric types (hardware + enrollment)
        if (hasFingerprintEnrolled(context)) {
            availableBiometrics.add(BiometricType.FINGERPRINT)
            Logger.d("Fingerprint enrolled and detected")
        }
        
        if (hasFaceEnrolled(context)) {
            availableBiometrics.add(BiometricType.FACE)
            Logger.d("Face enrolled and detected")
        }
        
        if (hasIris(context)) {
            availableBiometrics.add(BiometricType.IRIS)
            Logger.d("Iris sensor detected")
        }
        
        Logger.d("Available and enrolled biometrics: $availableBiometrics")
        return availableBiometrics
    }
    
    /**
     * Checks if fingerprint sensor is available AND has enrolled fingerprints.
     */
    @Suppress("DEPRECATION")
    private fun hasFingerprintEnrolled(context: Context): Boolean {
        return try {
            // Check hardware
            if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                return false
            }
            
            // Check enrollment
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as? FingerprintManager
                fingerprintManager?.hasEnrolledFingerprints() ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.w("Error checking fingerprint enrollment", e)
            false
        }
    }
    
    /**
     * Checks if face sensor is available AND has enrolled face data.
     * Uses multiple detection methods for better accuracy.
     */
    private fun hasFaceEnrolled(context: Context): Boolean {
        return try {
            // First check if fingerprint is enrolled
            val fingerprintEnrolled = hasFingerprintEnrolled(context)
            
            // Check if biometric is available
            val biometricManager = BiometricManager.from(context)
            val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_WEAK)
            
            // If biometric authentication is available but fingerprint is not enrolled,
            // then face must be enrolled (or iris, but iris is rare)
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS && !fingerprintEnrolled) {
                Logger.d("Face enrolled detected via inference (biometric available, fingerprint not enrolled)")
                return true
            }
            
            // Method 2: Check PackageManager feature
            if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)) {
                // If feature exists and biometric is available, assume face is enrolled
                if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                    Logger.d("Face enrolled detected via PackageManager.FEATURE_FACE")
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Logger.w("Error checking face enrollment", e)
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
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_WEAK)
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }
}
