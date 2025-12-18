package com.definex.biometricsdk.auth

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.definex.biometricsdk.model.AuthResult
import com.definex.biometricsdk.model.BiometricType
import com.definex.biometricsdk.model.RiskReport
import com.definex.biometricsdk.model.SecurityPolicy
import com.definex.biometricsdk.security.SecurityPolicyEvaluator
import com.definex.biometricsdk.util.Logger

/**
 * Main entry point for the Biometric Security SDK.
 * Provides biometric authentication with security policy enforcement.
 */
class BiometricAuthenticator {
    
    private var securityPolicy: SecurityPolicy = SecurityPolicy.permissive()
    
    /**
     * Sets the security policy to enforce during authentication.
     * 
     * @param policy The security policy to enforce
     */
    fun setSecurityPolicy(policy: SecurityPolicy) {
        this.securityPolicy = policy
        Logger.d("Security policy updated: $policy")
    }
    
    /**
     * Authenticates the user using biometric authentication.
     * 
     * @param context The FragmentActivity context
     * @param requiredBiometric The specific biometric type required, or null for any
     * @param challenge Optional challenge string for cryptographic operations
     * @param callback Callback for authentication result
     */
    fun authenticate(
        context: FragmentActivity,
        requiredBiometric: BiometricType? = null,
        challenge: String? = null,
        callback: (AuthResult) -> Unit
    ) {
        Logger.d("Starting biometric authentication")
        
        // Step 1: Check security policy
        val (isViolated, riskReport) = SecurityPolicyEvaluator.checkPolicy(context, securityPolicy)
        if (isViolated) {
            Logger.w("Security policy violation detected, authentication blocked")
            callback(AuthResult.Error.SecurityViolation(riskReport))
            return
        }
        
        // Step 2: Check if required biometric is available
        if (requiredBiometric != null) {
            val availableBiometrics = CapabilityChecker.getAvailableBiometrics(context)
            if (!availableBiometrics.contains(requiredBiometric)) {
                Logger.w("Required biometric $requiredBiometric not available")
                callback(AuthResult.Error.BiometricNotSupported(requiredBiometric))
                return
            }
        } else {
            // Check if any biometric is available
            if (!CapabilityChecker.isAnyBiometricAvailable(context)) {
                Logger.w("No biometric authentication available")
                callback(AuthResult.Error.AuthenticationError(
                    errorCode = BiometricPrompt.ERROR_NO_BIOMETRICS,
                    errorMessage = "No biometric authentication available on this device"
                ))
                return
            }
        }
        
        // Step 3: Show biometric prompt
        val promptManager = BiometricPromptManager(context)
        
        // If challenge is provided, we could use it with a CryptoObject
        // For now, authenticate without crypto object
        val cryptoObject: BiometricPrompt.CryptoObject? = null
        
        promptManager.authenticate(
            requiredBiometric = requiredBiometric,
            cryptoObject = cryptoObject,
            callback = callback
        )
    }
    
    /**
     * Evaluates the security risk of the current device.
     * 
     * @param context Android context
     * @return RiskReport containing detected security risks
     */
    fun evaluateRisk(context: Context): RiskReport {
        Logger.d("Evaluating device security risk")
        return SecurityPolicyEvaluator.evaluateRisk(context)
    }
    
    /**
     * Gets the available biometric types on the device.
     * 
     * @param context Android context
     * @return Set of available BiometricType
     */
    fun getAvailableBiometrics(context: Context): Set<BiometricType> {
        return CapabilityChecker.getAvailableBiometrics(context)
    }
    
    /**
     * Checks if a specific biometric type is available.
     * 
     * @param context Android context
     * @param type The biometric type to check
     * @return true if the biometric type is available
     */
    fun isBiometricAvailable(context: Context, type: BiometricType): Boolean {
        return CapabilityChecker.isBiometricAvailable(context, type)
    }
    
    /**
     * Enables or disables debug logging.
     * Should only be enabled during development.
     * 
     * @param enabled true to enable debug logging
     */
    fun setDebugLogging(enabled: Boolean) {
        Logger.setDebugEnabled(enabled)
    }
}
