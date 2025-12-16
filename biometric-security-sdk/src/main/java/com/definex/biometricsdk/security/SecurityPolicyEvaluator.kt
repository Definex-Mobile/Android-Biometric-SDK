package com.definex.biometricsdk.security

import android.content.Context
import com.definex.biometricsdk.model.RiskReport
import com.definex.biometricsdk.model.SecurityPolicy
import com.definex.biometricsdk.util.Logger

/**
 * Evaluates security policies and generates risk reports.
 * Coordinates all security detectors to assess device security.
 */
internal object SecurityPolicyEvaluator {
    
    /**
     * Evaluates the device security and generates a comprehensive risk report.
     * @param context Android context
     * @return RiskReport containing all detected security risks
     */
    fun evaluateRisk(context: Context): RiskReport {
        Logger.d("Evaluating device security risks")
        
        val rooted = RootDetector.isRooted()
        val emulator = EmulatorDetector.isEmulator()
        val hookingDetected = HookDetector.isHooked()
        val debugMode = DebugDetector.isDebuggable(context)
        
        val report = RiskReport(
            rooted = rooted,
            emulator = emulator,
            hookingDetected = hookingDetected,
            debugMode = debugMode
        )
        
        Logger.d("Risk report: rooted=$rooted, emulator=$emulator, hooked=$hookingDetected, debug=$debugMode")
        
        return report
    }
    
    /**
     * Checks if the device violates the given security policy.
     * @param context Android context
     * @param policy Security policy to enforce
     * @return Pair of (isViolated, RiskReport)
     */
    fun checkPolicy(context: Context, policy: SecurityPolicy): Pair<Boolean, RiskReport> {
        val report = evaluateRisk(context)
        
        val isViolated = (policy.disallowRootedDevices && report.rooted) ||
                (policy.disallowEmulators && report.emulator) ||
                (policy.disallowHookedDevices && report.hookingDetected) ||
                (policy.disallowDebuggableApps && report.debugMode)
        
        if (isViolated) {
            Logger.w("Security policy violation detected")
            val violations = mutableListOf<String>()
            if (policy.disallowRootedDevices && report.rooted) violations.add("rooted device")
            if (policy.disallowEmulators && report.emulator) violations.add("emulator")
            if (policy.disallowHookedDevices && report.hookingDetected) violations.add("hooking framework")
            if (policy.disallowDebuggableApps && report.debugMode) violations.add("debug mode")
            Logger.w("Violations: ${violations.joinToString(", ")}")
        }
        
        return Pair(isViolated, report)
    }
}

