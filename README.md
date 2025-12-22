# Android Biometric Security SDK

A commercial-grade Android SDK for biometric authentication with comprehensive security features including root detection, emulator detection, hook detection, and hardware-backed encryption.

## Features

### 🔐 Biometric Authentication
- Unified API for biometric authentication using system BiometricPrompt
- Support for specific biometric types (Fingerprint, Face, Iris)
- Automatic capability detection
- Hardware-backed cryptographic operations

### 🛡️ Security Detection
- **Root Detection**: Detects su binary, Magisk, dangerous system properties, and test-keys
- **Hook Detection**: Detects Xposed, EdXposed, LSPosed, Frida, and Riru/Zygisk
- **Emulator Detection**: Identifies Android emulators and virtual devices
- **Debug Detection**: Checks if the app is running in debug mode

### 🔒 Secure Storage
- Hardware-backed AES-GCM encryption
- EncryptedSharedPreferences for secure data storage
- Biometric-bound encryption keys
- Automatic key invalidation on biometric enrollment changes

### ⚙️ Security Policy System
- Configurable security policies
- Enforce authentication restrictions based on device security state
- Pre-defined policy templates (Permissive, Moderate, Strict)

## Requirements

- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.20+
- **AndroidX Biometric**: 1.2.0-alpha05+


## Installation

### Step 1: Add JitPack Repository

Add JitPack to your projects settings.gradle.kts:

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

Add the SDK dependency to your apps build.gradle.kts:

```kotlin
dependencies {
    implementation("com.github.Definex-Mobile:Android-Biometric-SDK:1.0.0")
}
```

### Step 3: Add Permissions

Add biometric permissions to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.USE_FINGERPRINT" />
```

## Quick Start

### Basic Biometric Authentication

```kotlin
import com.definex.biometricsdk.auth.BiometricAuthenticator
import com.definex.biometricsdk.model.AuthResult

class MainActivity : FragmentActivity() {
    
    private val biometricAuthenticator = BiometricAuthenticator()
    
    fun authenticate() {
        biometricAuthenticator.authenticate(
            context = this,
            // System automatically chooses available biometric
            challenge = null
        ) { result ->
            when (result) {
                is AuthResult.Success -> {
                    // Authentication successful
                    showSuccess()
                }
                is AuthResult.Failed -> {
                    // Biometric not recognized
                    showError("Authentication failed")
                }
                is AuthResult.Error -> {
                    // Handle error
                    handleError(result)
                }
            }
        }
    }
}
```

### Enforce Specific Biometric Type
### Check Available Biometrics

```kotlin
val availableBiometrics = biometricAuthenticator.getAvailableBiometrics(context)

if (availableBiometrics.contains(BiometricType.FACE)) {
    // Face authentication is available
}

if (availableBiometrics.contains(BiometricType.FINGERPRINT)) {
    // Fingerprint authentication is available
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

- `authenticate(context, requiredBiometric, challenge, callback)` - Authenticate user with biometrics
- `evaluateRisk(context)` - Evaluate device security risks
- `setSecurityPolicy(policy)` - Set security policy to enforce
- `getAvailableBiometrics(context)` - Get available biometric types
- `isBiometricAvailable(context, type)` - Check if specific biometric is available
- `setDebugLogging(enabled)` - Enable/disable debug logging

### BiometricType

Enum representing biometric types:
- `FINGERPRINT` - Fingerprint authentication
- `FACE` - Face authentication
- `IRIS` - Iris authentication

### AuthResult

Sealed class representing authentication results:

- `AuthResult.Success(cryptoObject)` - Authentication successful
- `AuthResult.Failed` - Biometric not recognized
- `AuthResult.Error.BiometricNotSupported(type)` - Required biometric not available
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
- `hasAnyRisk()` - Returns true if any risk detected
- `getDetectedRisks()` - Returns list of detected risks as strings

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
- `SecurityPolicy.permissive()` - Allow all devices
- `SecurityPolicy.moderate()` - Block rooted and hooked devices
- `SecurityPolicy.strict()` - Block all security risks

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
        is AuthResult.Error.BiometricNotSupported -> handleNotSupported(result.type)
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

```

## Sample App

The SDK includes a comprehensive sample app demonstrating all features:

- Biometric authentication with different types
- Device capability detection
- Security risk assessment
- Security policy configuration
- Encrypted storage operations

To run the sample app:

```bash
./gradlew :sample-app:installDebug
```

## Architecture

The SDK is organized into the following packages:

- `auth/` - Biometric authentication components
- `crypto/` - Encryption and key management
- `security/` - Security detectors (root, hook, emulator, debug)
- `model/` - Data models and sealed classes
- `util/` - Utility functions and extensions

## Limitations

1. **Biometric Type Detection**: Face and Iris detection relies on `PackageManager` features which may not be accurate on all devices. Fingerprint detection is more reliable.

2. **Security Detection**: Security detectors use heuristics and may produce false positives or miss sophisticated attacks. They should be used as part of a defense-in-depth strategy.

3. **Hardware Backing**: Encryption keys are hardware-backed on devices with Trusted Execution Environment (TEE) or Secure Element. On devices without hardware security, keys are software-backed.

## License

This is a commercial SDK. Please contact the vendor for licensing information.

## Support

For issues, questions, or feature requests, please contact support or open an issue in the project repository.

## Version History

### Version 1.0.0
- Initial release
- Biometric authentication with type enforcement
- Security detection (root, hook, emulator, debug)
- Hardware-backed encryption
- Secure storage
- Security policy system

