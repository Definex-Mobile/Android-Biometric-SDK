# Android Biometric Security SDK

A production-ready Android SDK for biometric authentication with comprehensive security features including root detection, emulator detection, hook detection, and configurable security policies.

## Features

### 🔐 Biometric Authentication
- Unified API for biometric authentication using system BiometricPrompt
- System automatically selects available enrolled biometric (fingerprint, face, iris)
- Automatic capability and enrollment detection
- Support for both strong and weak biometrics (BIOMETRIC_WEAK)

### 🛡️ Security Detection
- **Root Detection**: Detects su binary, Magisk, dangerous system properties, and test-keys
- **Hook Detection**: Detects Xposed, EdXposed, LSPosed, Frida, and Riru/Zygisk
- **Emulator Detection**: Identifies Android emulators and virtual devices
- **Debug Detection**: Checks if the app is running in debug mode

### ⚙️ Security Policy System
- Configurable security policies
- Enforce authentication restrictions based on device security state
- Pre-defined policy templates (Permissive, Moderate, Strict)
- Custom policy configuration support

## Requirements

- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.20+
- **Java**: 11+
- **AndroidX Biometric**: 1.2.0-alpha05+

## Installation

### Step 1: Add JitPack Repository

Add JitPack to your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add Dependency

Add the SDK dependency to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Definex-Mobile:Android-Biometric-SDK:1.0.3")
}
```

### Step 3: Add Permissions

Add biometric permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.USE_FINGERPRINT" />
```

## Quick Start

### Basic Biometric Authentication

```kotlin
import com.definex.biometricsdk.auth.BiometricAuthenticator
import com.definex.biometricsdk.model.AuthResult
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private val biometricAuthenticator = BiometricAuthenticator()
    
    fun authenticate() {
        biometricAuthenticator.authenticate(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    // Authentication successful
                    showSuccess()
                }
                is AuthResult.Failed -> {
                    // Biometric not recognized
                    showError("Authentication failed")
                }
                is AuthResult.Error.SecurityViolation -> {
                    // Security policy violation
                    handleSecurityViolation(result.report)
                }
                is AuthResult.Error.AuthenticationError -> {
                    // Handle error
                    handleError(result.errorCode, result.errorMessage)
                }
            }
        }
    }
}
```

### Check Available Biometrics

```kotlin
val availableBiometrics = biometricAuthenticator.getAvailableBiometrics(context)

if (availableBiometrics.contains(BiometricType.FACE)) {
    // Face authentication is available and enrolled
}

if (availableBiometrics.contains(BiometricType.FINGERPRINT)) {
    // Fingerprint authentication is available and enrolled
}

if (availableBiometrics.isEmpty()) {
    // No biometric authentication available
}
```

### Security Risk Assessment

```kotlin
val riskReport = biometricAuthenticator.evaluateRisk(context)

if (riskReport.rooted) {
    // Device is rooted
}

if (riskReport.emulator) {
    // Running on emulator
}

if (riskReport.hookingDetected) {
    // Hooking framework detected
}

if (riskReport.debugMode) {
    // App is debuggable
}

// Get human-readable risk list
val risks = riskReport.getDetectedRisks()
// Returns: ["Device is rooted", "Hooking framework detected", ...]
```

### Configure Security Policy

```kotlin
import com.definex.biometricsdk.model.SecurityPolicy

// Strict policy - block all security risks
val strictPolicy = SecurityPolicy.strict()
biometricAuthenticator.setSecurityPolicy(strictPolicy)

// Moderate policy - block rooted devices and hooking
val moderatePolicy = SecurityPolicy.moderate()
biometricAuthenticator.setSecurityPolicy(moderatePolicy)

// Custom policy
val customPolicy = SecurityPolicy(
    disallowRootedDevices = true,
    disallowEmulators = false,
    disallowHookedDevices = true,
    disallowDebuggableApps = false
)
biometricAuthenticator.setSecurityPolicy(customPolicy)
```

When a security policy is violated, authentication will fail with `AuthResult.Error.SecurityViolation`:

```kotlin
biometricAuthenticator.authenticate(this) { result ->
    when (result) {
        is AuthResult.Error.SecurityViolation -> {
            val report = result.report
            // Show which security checks failed
            val violations = report.getDetectedRisks()
            showError("Security violation: ${violations.joinToString()}")
        }
        // ... handle other results
    }
}
```

## API Reference

### BiometricAuthenticator

Main entry point for the SDK.

#### Methods

- `authenticate(context: AppCompatActivity, callback: (AuthResult) -> Unit)` - Authenticate user with biometrics
- `evaluateRisk(context: Context): RiskReport` - Evaluate device security risks
- `setSecurityPolicy(policy: SecurityPolicy)` - Set security policy to enforce
- `getAvailableBiometrics(context: Context): Set<BiometricType>` - Get available and enrolled biometric types
- `isBiometricAvailable(context: Context, type: BiometricType): Boolean` - Check if specific biometric is available
- `setDebugLogging(enabled: Boolean)` - Enable/disable debug logging

### BiometricType

Enum representing biometric types:
- `FINGERPRINT` - Fingerprint authentication
- `FACE` - Face authentication
- `IRIS` - Iris authentication

### AuthResult

Sealed class representing authentication results:

- `AuthResult.Success(cryptoObject)` - Authentication successful
- `AuthResult.Failed` - Biometric not recognized
- `AuthResult.Error.SecurityViolation(report)` - Security policy violated
- `AuthResult.Error.AuthenticationError(errorCode, errorMessage)` - Authentication error

### RiskReport

Data class containing security assessment:

```kotlin
data class RiskReport(
    val rooted: Boolean,
    val emulator: Boolean,
    val hookingDetected: Boolean,
    val debugMode: Boolean
)
```

Methods:
- `hasAnyRisk(): Boolean` - Returns true if any risk detected
- `getDetectedRisks(): List<String>` - Returns list of detected risks as strings

### SecurityPolicy

Data class for security policy configuration:

```kotlin
data class SecurityPolicy(
    val disallowRootedDevices: Boolean = false,
    val disallowEmulators: Boolean = false,
    val disallowHookedDevices: Boolean = false,
    val disallowDebuggableApps: Boolean = false
)
```

Static methods:
- `SecurityPolicy.permissive()` - Allow all devices (default)
- `SecurityPolicy.moderate()` - Block rooted and hooked devices
- `SecurityPolicy.strict()` - Block all security risks

## Best Practices

### 1. Always Use AppCompatActivity

```kotlin
// ✅ Correct
class MainActivity : AppCompatActivity() {
    fun authenticate() {
        biometricAuthenticator.authenticate(this) { result -> }
    }
}

// ❌ Wrong - Context is not supported
fun authenticate(context: Context) {
    biometricAuthenticator.authenticate(context) { result -> } // Won't compile
}
```

**Why?** BiometricPrompt API requires `AppCompatActivity` for lifecycle management and DialogFragment support.

### 2. Check Capabilities Before Authenticating

```kotlin
val availableBiometrics = biometricAuthenticator.getAvailableBiometrics(context)
if (availableBiometrics.isEmpty()) {
    // No biometric authentication available
    showError("Biometric authentication not available")
    return
}
```

### 3. Handle All Error Cases

```kotlin
biometricAuthenticator.authenticate(this) { result ->
    when (result) {
        is AuthResult.Success -> handleSuccess()
        is AuthResult.Failed -> handleFailure()
        is AuthResult.Error.SecurityViolation -> handleSecurityViolation(result.report)
        is AuthResult.Error.AuthenticationError -> handleError(result.errorCode, result.errorMessage)
    }
}
```

### 4. Disable Debug Logging in Production

```kotlin
// Only enable in debug builds
if (BuildConfig.DEBUG) {
    biometricAuthenticator.setDebugLogging(true)
}
```

## Sample App

The SDK includes a comprehensive sample app demonstrating all features:

- Biometric authentication
- Device capability detection
- Security risk assessment
- Security policy configuration

To run the sample app:

```bash
./gradlew :sample-app:installDebug
```

## Architecture

The SDK is organized into the following packages:

- `auth/` - Biometric authentication components
  - `BiometricAuthenticator` - Main SDK entry point
  - `BiometricPromptManager` - BiometricPrompt wrapper
  - `CapabilityChecker` - Biometric capability and enrollment detection
- `security/` - Security detectors (root, hook, emulator, debug)
  - `RootDetector` - Root detection
  - `HookDetector` - Hook framework detection
  - `EmulatorDetector` - Emulator detection
  - `DebugDetector` - Debug mode detection
  - `SecurityPolicyEvaluator` - Policy enforcement
- `model/` - Data models and sealed classes
  - `AuthResult` - Authentication result sealed class
  - `BiometricType` - Biometric type enum
  - `RiskReport` - Security risk report
  - `SecurityPolicy` - Security policy configuration
- `util/` - Utility functions and extensions
  - `Logger` - Safe logging utility

## Limitations

1. **Biometric Type Detection**: Face and Iris detection relies on `PackageManager` features which may not be accurate on all devices. Fingerprint detection is more reliable using `BiometricManager`.

2. **System-Controlled Biometric Selection**: The Android system automatically selects which biometric to show. The SDK cannot force a specific biometric type to be displayed.

3. **Security Detection**: Security detectors use heuristics and may produce false positives or miss sophisticated attacks. They should be used as part of a defense-in-depth strategy.

4. **AppCompatActivity Requirement**: Due to Android's BiometricPrompt API design, `AppCompatActivity` is required. Regular `Context` or `Activity` cannot be used.

## License

This SDK is open source. See LICENSE file for details.

## Support

For issues, questions, or feature requests, please open an issue on GitHub.

## Version History

### Version 1.0.3 (Current)
- Simplified authentication API by removing unused `challenge` parameter
- Removed `cryptoObject` parameter (not used)
- Cleaner, more intuitive API surface

### Version 1.0.2
- Fixed Gradle wrapper for JitPack build
- Excluded sample-app from JitPack build (library only)
- Improved face biometric detection with enrollment checks

### Version 1.0.1
- Added USE_FINGERPRINT permission
- Changed BIOMETRIC_STRONG to BIOMETRIC_WEAK for face support

### Version 1.0.0
- Initial release
- Biometric authentication with automatic type selection
- Security detection (root, hook, emulator, debug)
- Security policy system
- Capability and enrollment detection
