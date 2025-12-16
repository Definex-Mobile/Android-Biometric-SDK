package com.definex.biometricsdk.auth

import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.definex.biometricsdk.model.AuthResult
import com.definex.biometricsdk.model.BiometricType
import com.definex.biometricsdk.util.Logger

/**
 * Manages BiometricPrompt creation and authentication flow.
 * Wraps the system BiometricPrompt API with a simpler interface.
 */
internal class BiometricPromptManager(
    private val activity: FragmentActivity
) {
    
    /**
     * Shows the biometric prompt for authentication.
     * 
     * @param requiredBiometric The specific biometric type required, or null for any
     * @param cryptoObject Optional crypto object for cryptographic operations
     * @param callback Callback for authentication result
     */
    fun authenticate(
        requiredBiometric: BiometricType?,
        cryptoObject: BiometricPrompt.CryptoObject? = null,
        callback: (AuthResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val authCallback = object : AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Logger.d("Biometric authentication succeeded")
                callback(AuthResult.Success(result.cryptoObject))
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Logger.d("Biometric authentication failed")
                callback(AuthResult.Failed)
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Logger.w("Biometric authentication error: $errorCode - $errString")
                callback(
                    AuthResult.Error.AuthenticationError(
                        errorCode = errorCode,
                        errorMessage = errString.toString()
                    )
                )
            }
        }
        
        val biometricPrompt = BiometricPrompt(activity, executor, authCallback)
        val promptInfo = buildPromptInfo(requiredBiometric)
        
        Logger.d("Showing biometric prompt")
        if (cryptoObject != null) {
            biometricPrompt.authenticate(promptInfo, cryptoObject)
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }
    
    /**
     * Builds the PromptInfo configuration for BiometricPrompt.
     */
    private fun buildPromptInfo(requiredBiometric: BiometricType?): PromptInfo {
        val builder = PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to continue")
            .setNegativeButtonText("Cancel")
        
        // Set description based on required biometric type
        when (requiredBiometric) {
            BiometricType.FINGERPRINT -> {
                builder.setDescription("Use your fingerprint to authenticate")
            }
            BiometricType.FACE -> {
                builder.setDescription("Use your face to authenticate")
            }
            BiometricType.IRIS -> {
                builder.setDescription("Use your iris to authenticate")
            }
            null -> {
                builder.setDescription("Use your biometric to authenticate")
            }
        }
        
        // Set allowed authenticators
        // Always use BIOMETRIC_STRONG for maximum security
        builder.setAllowedAuthenticators(BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC)
        
        return builder.build()
    }
}

