package com.definex.biometricsdk.util

import android.util.Log

/**
 * Internal logger for the SDK with safe logging practices.
 * Only logs in debug builds to prevent information leakage.
 */
internal object Logger {
    
    private const val TAG = "BiometricSDK"
    private var isDebugEnabled = false
    
    /**
     * Enable or disable debug logging.
     * Should only be enabled during development.
     */
    fun setDebugEnabled(enabled: Boolean) {
        isDebugEnabled = enabled
    }
    
    /**
     * Log a debug message.
     */
    fun d(message: String) {
        if (isDebugEnabled) {
            Log.d(TAG, message)
        }
    }
    
    /**
     * Log an info message.
     */
    fun i(message: String) {
        if (isDebugEnabled) {
            Log.i(TAG, message)
        }
    }
    
    /**
     * Log a warning message.
     */
    fun w(message: String, throwable: Throwable? = null) {
        if (isDebugEnabled) {
            if (throwable != null) {
                Log.w(TAG, message, throwable)
            } else {
                Log.w(TAG, message)
            }
        }
    }
    
    /**
     * Log an error message.
     * Errors are always logged regardless of debug mode.
     */
    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }
}

