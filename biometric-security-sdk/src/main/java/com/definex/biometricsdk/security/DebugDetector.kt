package com.definex.biometricsdk.security

import android.content.Context
import android.content.pm.ApplicationInfo
import com.definex.biometricsdk.util.Logger

/**
 * Detects if the application is running in debug mode.
 */
internal object DebugDetector {
    
    /**
     * Checks if the application is debuggable.
     * @param context Android context
     * @return true if the application is debuggable
     */
    fun isDebuggable(context: Context): Boolean {
        return try {
            val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (isDebuggable) {
                Logger.d("Debug mode detected: application is debuggable")
            }
            isDebuggable
        } catch (e: Exception) {
            Logger.w("Error checking debug mode", e)
            false
        }
    }
}

