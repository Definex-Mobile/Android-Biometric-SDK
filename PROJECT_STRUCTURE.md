# Android Biometric Security SDK - Project Structure

## Overview

This project contains a production-ready Android Biometric Security SDK with a comprehensive sample application demonstrating all features.

## Project Structure

```
Android-Biometric-SDK/
├── biometric-security-sdk/              # SDK Library Module
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   └── java/com/definex/biometricsdk/
│   │       ├── auth/                    # Biometric Authentication
│   │       │   ├── BiometricAuthenticator.kt      (Public API - Main entry point)
│   │       │   ├── BiometricPromptManager.kt      (BiometricPrompt wrapper)
│   │       │   └── CapabilityChecker.kt           (Capability & enrollment detection)
│   │       ├── crypto/                  # Encryption & Key Management
│   │       │   ├── KeyStoreManager.kt             (Hardware-backed keys)
│   │       │   └── CipherProvider.kt              (AES-GCM cipher)
│   │       ├── model/                   # Data Models
│   │       │   ├── BiometricType.kt               (Enum: FINGERPRINT, FACE, IRIS)
│   │       │   ├── AuthResult.kt                  (Sealed class for auth results)
│   │       │   ├── RiskReport.kt                  (Security assessment data)
│   │       │   ├── SecurityPolicy.kt              (Policy configuration)
│   │       │   └── EncryptedPayload.kt            (Encrypted data container)
│   │       ├── security/                # Security Detectors
│   │       │   ├── RootDetector.kt                (Root detection)
│   │       │   ├── HookDetector.kt                (Hook framework detection)
│   │       │   ├── EmulatorDetector.kt            (Emulator detection)
│   │       │   ├── DebugDetector.kt               (Debug mode detection)
│   │       │   └── SecurityPolicyEvaluator.kt     (Policy enforcement)
│   │       └── util/                    # Utilities
│   │           ├── Logger.kt                      (Safe logging)
│   │           └── Extensions.kt                  (Kotlin extensions)
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   ├── consumer-rules.pro
│   └── README.md
│
├── sample-app/                          # Sample Application
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/definex/biometricsdk/sample/
│   │   │   └── MainActivity.kt                    (Demo UI)
│   │   └── res/
│   │       ├── layout/
│   │       │   └── activity_main.xml              (Main UI layout)
│   │       ├── values/
│   │       │   ├── strings.xml
│   │       │   ├── colors.xml
│   │       │   └── themes.xml
│   │       └── drawable/
│   │           └── ic_launcher_background.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts                     # Root build configuration
├── settings.gradle.kts                  # Module configuration
├── gradle.properties                    # Gradle properties
├── jitpack.yml                          # JitPack build configuration
├── .gitignore
├── README.md                            # Main documentation
└── PROJECT_STRUCTURE.md                 # This file
```

## Module Details

### biometric-security-sdk (Library)

**Package**: `com.definex.biometricsdk`

**Key Features**:
- Biometric authentication with automatic type selection
- System-controlled biometric selection (fingerprint, face, iris)
- Enrollment verification for fingerprint and face
- Root, Hook, Emulator, and Debug detection
- Configurable security policies with pre-defined templates
- Comprehensive risk reporting

**Public API**:
- `BiometricAuthenticator` - Main SDK entry point
- `BiometricType` - Enum for biometric types
- `AuthResult` - Sealed class for authentication results
- `SecurityPolicy` - Security policy configuration
- `RiskReport` - Security risk assessment

**Dependencies**:
- androidx.biometric:biometric:1.2.0-alpha05
- androidx.security:security-crypto:1.1.0-alpha06
- androidx.core:core-ktx:1.12.0
- kotlinx-coroutines-android:1.7.3

### sample-app (Application)

**Package**: `com.definex.biometricsdk.sample`

**Features**:
- Biometric authentication demo
- Device capability detection display
- Security risk assessment display
- Security policy configuration UI
- Real-time authentication feedback

**Dependencies**:
- biometric-security-sdk (module dependency)
- Material Components
- AppCompat

## Build Configuration

- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Kotlin**: 1.9.20
- **Gradle**: 8.4
- **Android Gradle Plugin**: 8.3.2
- **JVM Target**: 11

## Getting Started

### Build the Project

```bash
./gradlew build
```

### Build SDK Only

```bash
./gradlew :biometric-security-sdk:assemble
```

### Run Sample App

```bash
./gradlew :sample-app:installDebug
```

### Run Tests

```bash
./gradlew test
```

## Implementation Highlights

### 1. System-Controlled Biometric Selection

The SDK uses Android's BiometricPrompt which automatically selects the best available biometric:
1. System checks enrolled biometrics (fingerprint, face, iris)
2. System presents appropriate UI based on available sensors
3. SDK cannot force a specific biometric type to be displayed
4. SDK can detect which types are enrolled via `getAvailableBiometrics()`

### 2. Enrollment Detection

**Fingerprint**:
- Uses `BiometricManager.canAuthenticate()` for modern detection
- Falls back to hardware feature check if needed
- Deprecated `FingerprintManager.hasEnrolledFingerprints()` suppressed

**Face**:
- Inference-based detection: if biometric available but fingerprint not enrolled, face must be enrolled
- PackageManager.FEATURE_FACE check as secondary method
- Works on most devices despite manufacturer inconsistencies

**Iris**:
- Hardware feature check only (rare on consumer devices)

### 3. Security Policy System

Three-tier policy system:
- **Permissive**: Allow all devices (default)
- **Moderate**: Block rooted and hooked devices
- **Strict**: Block all security risks (root, emulator, hooks, debug)
- **Custom**: Configure individual restrictions

Policy enforcement happens before authentication:
1. Evaluate device security
2. Check against policy
3. Block with `SecurityViolation` error if policy violated
4. Proceed with authentication if policy allows

### 4. Security Detection

**Root Detection**:
- su binary in common paths (/system/bin, /system/xbin, etc.)
- Magisk directories and files
- Dangerous system properties (ro.debuggable, ro.secure)
- Test-keys in build tags

**Hook Detection**:
- XposedBridge class loading attempt
- EdXposed/LSPosed detection
- Riru/Zygisk detection
- Frida server detection (port 27042)
- Suspicious loaded libraries

**Emulator Detection**:
- QEMU kernel property check
- Known emulator manufacturers (Genymotion, Google)
- Generic device fingerprints
- Suspicious hardware names

**Debug Detection**:
- ApplicationInfo.FLAG_DEBUGGABLE check

### 5. API Design

**Simplified API** (v1.0.3+):
```kotlin
// Clean, minimal API surface
authenticate(context: AppCompatActivity, callback: (AuthResult) -> Unit)
```

**AppCompatActivity Requirement**:
- Required by Android's BiometricPrompt API
- Needed for DialogFragment lifecycle management
- Cannot use regular Context or Activity

**No Unused Parameters**:
- Removed `challenge` parameter (not used)
- Removed `cryptoObject` parameter (not used)
- Removed `requiredBiometric` parameter (system controls selection)

## API Usage Examples

### Basic Authentication

```kotlin
val authenticator = BiometricAuthenticator()
authenticator.authenticate(this) { result ->
    when (result) {
        is AuthResult.Success -> println("Success!")
        is AuthResult.Failed -> println("Failed")
        is AuthResult.Error.SecurityViolation -> println("Security violation!")
        is AuthResult.Error.AuthenticationError -> println("Error: ${result.errorMessage}")
    }
}
```

### Check Capabilities

```kotlin
val availableBiometrics = authenticator.getAvailableBiometrics(context)
// Returns Set<BiometricType> of enrolled biometrics
```

### Security Policy

```kotlin
authenticator.setSecurityPolicy(SecurityPolicy.strict())
```

### Risk Assessment

```kotlin
val report = authenticator.evaluateRisk(context)
if (report.hasAnyRisk()) {
    val risks = report.getDetectedRisks()
    println("Risks: ${risks.joinToString()}")
}
```

## Testing

The sample app provides comprehensive testing of all SDK features:

1. **Authentication Tests**: Test biometric authentication flow
2. **Capability Tests**: Check available and enrolled biometrics
3. **Security Tests**: Evaluate device security risks
4. **Policy Tests**: Configure and test security policies

## JitPack Integration

The SDK is published via JitPack for easy integration:

```kotlin
// settings.gradle.kts
maven { url = uri("https://jitpack.io") }

// build.gradle.kts
implementation("com.github.Definex-Mobile:Android-Biometric-SDK:1.0.3")
```

**JitPack Configuration** (`jitpack.yml`):
- Java 11 for build
- Excludes sample-app from build (library only)
- Maven publish plugin configured

## Notes

- All code is production-ready with comprehensive error handling
- Safe logging that respects debug mode
- Clean architecture with separation of concerns
- Well-documented public API
- ProGuard rules included for release builds
- No TODOs or placeholder code
- Deprecated APIs properly suppressed with explanations

## License

Open source. See LICENSE file for details.
