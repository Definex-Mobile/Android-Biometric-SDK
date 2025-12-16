package com.definex.biometricsdk.security

import com.definex.biometricsdk.util.Logger
import com.definex.biometricsdk.util.getBuildFingerprint
import com.definex.biometricsdk.util.getHardware
import com.definex.biometricsdk.util.getManufacturer
import com.definex.biometricsdk.util.getModel
import com.definex.biometricsdk.util.getProduct
import com.definex.biometricsdk.util.getSystemProperty

/**
 * Detects if the app is running on an emulator.
 * Checks for common emulator indicators including system properties and device characteristics.
 */
internal object EmulatorDetector {
    
    private val knownEmulatorManufacturers = setOf(
        "Genymotion",
        "unknown",
        "Google" // Android Emulator
    )
    
    private val suspiciousModels = setOf(
        "sdk",
        "google_sdk",
        "Emulator",
        "Android SDK built for x86",
        "Android SDK built for arm64",
        "Droid4X"
    )
    
    /**
     * Performs a comprehensive emulator detection check.
     * @return true if the device appears to be an emulator
     */
    fun isEmulator(): Boolean {
        return checkQemuProperty() ||
                checkManufacturer() ||
                checkModel() ||
                checkFingerprint() ||
                checkHardware() ||
                checkProduct()
    }
    
    /**
     * Checks for QEMU kernel property.
     */
    private fun checkQemuProperty(): Boolean {
        return try {
            val qemu = getSystemProperty("ro.kernel.qemu")
            if (qemu == "1") {
                Logger.d("Emulator detected: ro.kernel.qemu=1")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.w("Error checking QEMU property", e)
            false
        }
    }
    
    /**
     * Checks for known emulator manufacturers.
     */
    private fun checkManufacturer(): Boolean {
        return try {
            val manufacturer = getManufacturer()
            
            // Check if manufacturer is in known emulator list
            if (knownEmulatorManufacturers.any { it.equals(manufacturer, ignoreCase = true) }) {
                // Google manufacturer could be real Pixel devices, need additional checks
                if (manufacturer.equals("Google", ignoreCase = true)) {
                    val model = getModel()
                    // If it's a Google manufacturer with suspicious model, it's likely an emulator
                    if (suspiciousModels.any { model.contains(it, ignoreCase = true) }) {
                        Logger.d("Emulator detected: Google manufacturer with suspicious model")
                        return true
                    }
                    return false
                }
                
                Logger.d("Emulator detected: manufacturer=$manufacturer")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.w("Error checking manufacturer", e)
            false
        }
    }
    
    /**
     * Checks for suspicious device models.
     */
    private fun checkModel(): Boolean {
        return try {
            val model = getModel()
            if (suspiciousModels.any { model.contains(it, ignoreCase = true) }) {
                Logger.d("Emulator detected: suspicious model=$model")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.w("Error checking model", e)
            false
        }
    }
    
    /**
     * Checks for generic or emulator fingerprints.
     */
    private fun checkFingerprint(): Boolean {
        return try {
            val fingerprint = getBuildFingerprint().lowercase()
            val suspiciousFingerprints = listOf(
                "generic",
                "unknown",
                "emulator",
                "vbox",
                "test-keys"
            )
            
            if (suspiciousFingerprints.any { fingerprint.contains(it) }) {
                Logger.d("Emulator detected: suspicious fingerprint")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.w("Error checking fingerprint", e)
            false
        }
    }
    
    /**
     * Checks for generic hardware names.
     */
    private fun checkHardware(): Boolean {
        return try {
            val hardware = getHardware().lowercase()
            val suspiciousHardware = listOf(
                "goldfish",
                "ranchu",
                "vbox",
                "nox",
                "ttvm"
            )
            
            if (suspiciousHardware.any { hardware.contains(it) }) {
                Logger.d("Emulator detected: suspicious hardware=$hardware")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.w("Error checking hardware", e)
            false
        }
    }
    
    /**
     * Checks for generic product names.
     */
    private fun checkProduct(): Boolean {
        return try {
            val product = getProduct().lowercase()
            val suspiciousProducts = listOf(
                "sdk",
                "google_sdk",
                "sdk_x86",
                "vbox86p",
                "emulator",
                "simulator"
            )
            
            if (suspiciousProducts.any { product.contains(it) }) {
                Logger.d("Emulator detected: suspicious product=$product")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.w("Error checking product", e)
            false
        }
    }
}

