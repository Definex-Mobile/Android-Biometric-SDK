package com.definex.biometricsdk.security

import com.definex.biometricsdk.util.Logger
import com.definex.biometricsdk.util.existsAndCanRead
import com.definex.biometricsdk.util.safeReadText
import java.io.File

/**
 * Detects hooking frameworks and instrumentation tools.
 * Checks for Xposed, Frida, Riru/Zygisk and other hooking mechanisms.
 */
internal object HookDetector {
    
    private val xposedPaths = listOf(
        "/system/framework/XposedBridge.jar",
        "/system/lib/libxposed_art.so",
        "/system/lib64/libxposed_art.so"
    )
    
    private val riruPaths = listOf(
        "/data/adb/riru",
        "/dev/riru",
        "/system/lib/libriru.so",
        "/system/lib64/libriru.so"
    )
    
    private val edxposedPaths = listOf(
        "/system/framework/edxp.jar",
        "/system/lib/libedxp.so",
        "/system/lib64/libedxp.so"
    )
    
    private val lsposedPaths = listOf(
        "/data/adb/lspd",
        "/system/framework/lspd.jar"
    )
    
    /**
     * Performs a comprehensive hook detection check.
     * @return true if any hooking framework is detected
     */
    fun isHooked(): Boolean {
        return checkXposed() ||
                checkEdXposed() ||
                checkLSPosed() ||
                checkRiru() ||
                checkFrida() ||
                checkSuspiciousLibraries()
    }
    
    /**
     * Checks for Xposed framework.
     */
    private fun checkXposed(): Boolean {
        return try {
            // Try to load XposedBridge class
            try {
                Class.forName("de.robv.android.xposed.XposedBridge")
                Logger.d("Hook detected: XposedBridge class found")
                return true
            } catch (e: ClassNotFoundException) {
                // Class not found, continue checking
            }
            
            // Check for Xposed files
            xposedPaths.any { path ->
                val file = File(path)
                if (file.existsAndCanRead()) {
                    Logger.d("Hook detected: Xposed file found at $path")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking Xposed", e)
            false
        }
    }
    
    /**
     * Checks for EdXposed framework.
     */
    private fun checkEdXposed(): Boolean {
        return try {
            // Try to load EdXposed classes
            try {
                Class.forName("de.robv.android.xposed.XposedBridge")
                Class.forName("com.elderdrivers.riru.edxp.config.EdXpConfigGlobal")
                Logger.d("Hook detected: EdXposed classes found")
                return true
            } catch (e: ClassNotFoundException) {
                // Classes not found, continue checking
            }
            
            // Check for EdXposed files
            edxposedPaths.any { path ->
                val file = File(path)
                if (file.existsAndCanRead()) {
                    Logger.d("Hook detected: EdXposed file found at $path")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking EdXposed", e)
            false
        }
    }
    
    /**
     * Checks for LSPosed framework.
     */
    private fun checkLSPosed(): Boolean {
        return try {
            // Try to load LSPosed class
            try {
                Class.forName("org.lsposed.lspd.core.Main")
                Logger.d("Hook detected: LSPosed class found")
                return true
            } catch (e: ClassNotFoundException) {
                // Class not found, continue checking
            }
            
            // Check for LSPosed files
            lsposedPaths.any { path ->
                val file = File(path)
                if (file.exists()) {
                    Logger.d("Hook detected: LSPosed found at $path")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking LSPosed", e)
            false
        }
    }
    
    /**
     * Checks for Riru/Zygisk.
     */
    private fun checkRiru(): Boolean {
        return try {
            riruPaths.any { path ->
                val file = File(path)
                if (file.exists()) {
                    Logger.d("Hook detected: Riru/Zygisk found at $path")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking Riru", e)
            false
        }
    }
    
    /**
     * Checks for Frida server.
     */
    private fun checkFrida(): Boolean {
        return try {
            // Check for Frida server port
            val portFile = File("/proc/net/tcp")
            if (portFile.existsAndCanRead()) {
                val content = portFile.safeReadText()
                if (content?.contains("69CE") == true) { // 27042 in hex
                    Logger.d("Hook detected: Frida server port found")
                    return true
                }
            }
            
            // Check for Frida libraries
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.existsAndCanRead()) {
                val content = mapsFile.safeReadText()
                if (content?.contains("frida", ignoreCase = true) == true) {
                    Logger.d("Hook detected: Frida library found in memory maps")
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Logger.w("Error checking Frida", e)
            false
        }
    }
    
    /**
     * Checks for suspicious loaded libraries.
     */
    private fun checkSuspiciousLibraries(): Boolean {
        return try {
            val mapsFile = File("/proc/self/maps")
            if (!mapsFile.existsAndCanRead()) {
                return false
            }
            
            val content = mapsFile.safeReadText() ?: return false
            
            val suspiciousLibraries = listOf(
                "com.saurik.substrate",
                "XposedBridge.jar",
                "libsubstrate",
                "libxposed",
                "libfrida",
                "libriru",
                "libedxp"
            )
            
            suspiciousLibraries.any { lib ->
                if (content.contains(lib, ignoreCase = true)) {
                    Logger.d("Hook detected: suspicious library $lib found")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking suspicious libraries", e)
            false
        }
    }
}

