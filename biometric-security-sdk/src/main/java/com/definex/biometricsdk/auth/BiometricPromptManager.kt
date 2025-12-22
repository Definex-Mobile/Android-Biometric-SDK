package com.definex.biometricsdk.auth

import androidx.biometric.BiometricManager
import android.os.Build
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.definex.biometricsdk.model.AuthResult
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
     * The system will automatically use any available enrolled biometric.
     * 
     * @param cryptoObject Optional crypto object for cryptographic operations
     * @param callback Callback for authentication result
     */
    fun authenticate(
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
        val promptInfo = buildPromptInfo()
        
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
    private fun buildPromptInfo(): PromptInfo {
        val builder = PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to continue")
            .setNegativeButtonText("Cancel")
            .setDescription("Confirm your identity to continue")
        
        // Set allowed authenticators
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31+: Allow both strong and weak biometrics (includes face)
            builder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
        }
        
        return builder.build()
    }
}
