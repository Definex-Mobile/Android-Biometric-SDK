package com.definex.biometricsdk.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File

/**
 * Extension functions for the SDK.
 */

/**
 * Checks if a file exists and is accessible.
 */
internal fun File.existsAndCanRead(): Boolean {
    return try {
        exists() && canRead()
    } catch (e: SecurityException) {
        false
    }
}

/**
 * Safely reads a file's content.
 */
internal fun File.safeReadText(): String? {
    return try {
        if (existsAndCanRead()) readText() else null
    } catch (e: Exception) {
        null
    }
}

/**
 * Checks if the device has a specific hardware feature.
 */
internal fun Context.hasSystemFeature(feature: String): Boolean {
    return try {
        packageManager.hasSystemFeature(feature)
    } catch (e: Exception) {
        false
    }
}

/**
 * Gets a system property value safely.
 */
internal fun getSystemProperty(key: String): String? {
    return try {
        val process = Runtime.getRuntime().exec("getprop $key")
        process.inputStream.bufferedReader().use { it.readLine() }
    } catch (e: Exception) {
        null
    }
}

/**
 * Checks if a command exists in the system PATH.
 */
internal fun commandExists(command: String): Boolean {
    return try {
        val paths = listOf(
            "/system/bin/$command",
            "/system/xbin/$command",
            "/sbin/$command",
            "/vendor/bin/$command",
            "/system/sd/xbin/$command",
            "/system/bin/failsafe/$command",
            "/data/local/xbin/$command",
            "/data/local/bin/$command",
            "/data/local/$command"
        )
        paths.any { File(it).existsAndCanRead() }
    } catch (e: Exception) {
        false
    }
}

/**
 * Returns the device's build fingerprint safely.
 */
internal fun getBuildFingerprint(): String {
    return Build.FINGERPRINT ?: ""
}

/**
 * Returns the device's manufacturer safely.
 */
internal fun getManufacturer(): String {
    return Build.MANUFACTURER ?: ""
}

/**
 * Returns the device's model safely.
 */
internal fun getModel(): String {
    return Build.MODEL ?: ""
}

/**
 * Returns the device's product name safely.
 */
internal fun getProduct(): String {
    return Build.PRODUCT ?: ""
}

/**
 * Returns the device's hardware name safely.
 */
internal fun getHardware(): String {
    return Build.HARDWARE ?: ""
}

/**
 * Returns the build tags safely.
 */
internal fun getBuildTags(): String {
    return Build.TAGS ?: ""
}

