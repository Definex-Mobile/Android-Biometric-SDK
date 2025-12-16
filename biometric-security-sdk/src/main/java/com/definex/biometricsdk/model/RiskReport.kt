package com.definex.biometricsdk.model

/**
 * Data class representing a comprehensive security risk assessment of the device.
 * 
 * @property rooted Whether the device is rooted (has su access, Magisk, etc.)
 * @property emulator Whether the device is an emulator
 * @property hookingDetected Whether hooking frameworks (Xposed, Frida, etc.) are detected
 * @property debugMode Whether the application is running in debug mode
 */
data class RiskReport(
    val rooted: Boolean,
    val emulator: Boolean,
    val hookingDetected: Boolean,
    val debugMode: Boolean
) {
    /**
     * Returns true if any security risk is detected.
     */
    fun hasAnyRisk(): Boolean = rooted || emulator || hookingDetected || debugMode
    
    /**
     * Returns a list of detected risks as human-readable strings.
     */
    fun getDetectedRisks(): List<String> {
        val risks = mutableListOf<String>()
        if (rooted) risks.add("Device is rooted")
        if (emulator) risks.add("Running on emulator")
        if (hookingDetected) risks.add("Hooking framework detected")
        if (debugMode) risks.add("Application is debuggable")
        return risks
    }
}

