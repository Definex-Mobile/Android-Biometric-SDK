package com.definex.biometricsdk.security

import com.definex.biometricsdk.util.Logger
import com.definex.biometricsdk.util.commandExists
import com.definex.biometricsdk.util.existsAndCanRead
import com.definex.biometricsdk.util.getBuildTags
import com.definex.biometricsdk.util.getSystemProperty
import java.io.File

/**
 * Detects if the device is rooted.
 * Checks for common root indicators including su binary, Magisk, and dangerous system properties.
 */
internal object RootDetector {
    
    private val suPaths = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )
    
    private val magiskPaths = listOf(
        "/sbin/.magisk",
        "/sbin/.magisk/",
        "/cache/.disable_magisk",
        "/dev/.magisk.unblock",
        "/cache/magisk.log",
        "/data/adb/magisk",
        "/data/adb/magisk.db",
        "/data/adb/modules"
    )
    
    private val busyboxPaths = listOf(
        "/system/bin/busybox",
        "/system/xbin/busybox",
        "/sbin/busybox",
        "/data/local/busybox",
        "/data/local/bin/busybox",
        "/data/local/xbin/busybox"
    )
    
    /**
     * Performs a comprehensive root detection check.
     * @return true if any root indicator is detected
     */
    fun isRooted(): Boolean {
        return checkSuBinary() ||
                checkBusyBox() ||
                checkMagisk() ||
                checkDangerousProps() ||
                checkTestKeys()
    }
    
    /**
     * Checks for su binary in common locations.
     */
    private fun checkSuBinary(): Boolean {
        return try {
            // Check if su command exists
            if (commandExists("su")) {
                Logger.d("Root detected: su command found")
                return true
            }
            
            // Check common su paths
            suPaths.any { path ->
                val file = File(path)
                if (file.existsAndCanRead()) {
                    Logger.d("Root detected: su binary found at $path")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking su binary", e)
            false
        }
    }
    
    /**
     * Checks for busybox binary.
     */
    private fun checkBusyBox(): Boolean {
        return try {
            busyboxPaths.any { path ->
                val file = File(path)
                if (file.existsAndCanRead()) {
                    Logger.d("Root detected: busybox found at $path")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking busybox", e)
            false
        }
    }
    
    /**
     * Checks for Magisk installation.
     */
    private fun checkMagisk(): Boolean {
        return try {
            magiskPaths.any { path ->
                val file = File(path)
                if (file.exists()) {
                    Logger.d("Root detected: Magisk found at $path")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking Magisk", e)
            false
        }
    }
    
    /**
     * Checks for dangerous system properties that indicate root.
     */
    private fun checkDangerousProps(): Boolean {
        return try {
            val dangerousProps = mapOf(
                "ro.debuggable" to "1",
                "ro.secure" to "0"
            )
            
            dangerousProps.any { (key, dangerousValue) ->
                val value = getSystemProperty(key)
                if (value == dangerousValue) {
                    Logger.d("Root detected: dangerous property $key=$value")
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Logger.w("Error checking dangerous props", e)
            false
        }
    }
    
    /**
     * Checks if the build is signed with test keys.
     */
    private fun checkTestKeys(): Boolean {
        return try {
            val tags = getBuildTags()
            val hasTestKeys = tags.contains("test-keys")
            if (hasTestKeys) {
                Logger.d("Root detected: build signed with test-keys")
            }
            hasTestKeys
        } catch (e: Exception) {
            Logger.w("Error checking test keys", e)
            false
        }
    }
}

