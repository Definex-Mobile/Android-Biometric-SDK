package com.definex.biometricsdk.model

/**
 * Configuration class for security policy enforcement.
 * Defines which security violations should prevent biometric authentication.
 * 
 * @property disallowRootedDevices If true, authentication will fail on rooted devices
 * @property disallowEmulators If true, authentication will fail on emulators
 * @property disallowHookedDevices If true, authentication will fail when hooking is detected
 * @property disallowDebuggableApps If true, authentication will fail in debug builds
 */
data class SecurityPolicy(
    val disallowRootedDevices: Boolean = false,
    val disallowEmulators: Boolean = false,
    val disallowHookedDevices: Boolean = false,
    val disallowDebuggableApps: Boolean = false
) {
    companion object {
        /**
         * Returns a permissive policy that allows authentication in all scenarios.
         */
        fun permissive() = SecurityPolicy(
            disallowRootedDevices = false,
            disallowEmulators = false,
            disallowHookedDevices = false,
            disallowDebuggableApps = false
        )
        
        /**
         * Returns a strict policy that blocks authentication on any security risk.
         */
        fun strict() = SecurityPolicy(
            disallowRootedDevices = true,
            disallowEmulators = true,
            disallowHookedDevices = true,
            disallowDebuggableApps = true
        )
        
        /**
         * Returns a moderate policy that blocks rooted devices and hooking frameworks.
         */
        fun moderate() = SecurityPolicy(
            disallowRootedDevices = true,
            disallowEmulators = false,
            disallowHookedDevices = true,
            disallowDebuggableApps = false
        )
    }
}

