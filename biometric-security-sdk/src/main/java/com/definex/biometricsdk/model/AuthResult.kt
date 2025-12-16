package com.definex.biometricsdk.model

import androidx.biometric.BiometricPrompt

/**
 * Sealed class representing the result of a biometric authentication attempt.
 */
sealed class AuthResult {
    
    /**
     * Authentication was successful.
     * @param cryptoObject The crypto object used for authentication, if any
     */
    data class Success(
        val cryptoObject: BiometricPrompt.CryptoObject? = null
    ) : AuthResult()
    
    /**
     * Authentication failed (e.g., biometric not recognized).
     */
    data object Failed : AuthResult()
    
    /**
     * Base class for authentication errors.
     */
    sealed class Error : AuthResult() {
        
        /**
         * The required biometric type is not supported on this device.
         * @param type The biometric type that was required but not available
         */
        data class BiometricNotSupported(
            val type: BiometricType
        ) : Error()
        
        /**
         * A security policy violation was detected.
         * @param report The risk report detailing the security violations
         */
        data class SecurityViolation(
            val report: RiskReport
        ) : Error()
        
        /**
         * An authentication error occurred.
         * @param errorCode The error code from BiometricPrompt
         * @param errorMessage The error message describing what went wrong
         */
        data class AuthenticationError(
            val errorCode: Int,
            val errorMessage: String
        ) : Error()
    }
}

