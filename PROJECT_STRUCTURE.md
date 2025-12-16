# Android Biometric Security SDK - Project Structure

## Overview

This project contains a commercial-grade Android Biometric Security SDK with a comprehensive sample application.

## Project Structure

```
Android-Biometric-SDK/
├── biometric-security-sdk/              # SDK Library Module
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   └── java/com/definex/biometricsdk/
│   │       ├── auth/                    # Biometric Authentication
│   │       │   ├── BiometricAuthenticator.kt      (Public API)
│   │       │   ├── BiometricPromptManager.kt      (Prompt wrapper)
│   │       │   └── CapabilityChecker.kt           (Sensor detection)
│   │       ├── crypto/                  # Encryption & Key Management
│   │       │   ├── KeyStoreManager.kt             (Hardware-backed keys)
│   │       │   ├── CipherProvider.kt              (AES-GCM cipher)
│   │       │   └── SecureStorage.kt               (Encrypted storage)
│   │       ├── model/                   # Data Models
│   │       │   ├── BiometricType.kt               (Enum: FINGERPRINT, FACE, IRIS)
│   │       │   ├── AuthResult.kt                  (Sealed class)
│   │       │   ├── RiskReport.kt                  (Security assessment)
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
│   │       ├── drawable/
│   │       │   └── ic_launcher_foreground.xml
│   │       └── mipmap-anydpi-v26/
│   │           ├── ic_launcher.xml
│   │           └── ic_launcher_round.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts                     # Root build configuration
├── settings.gradle.kts                  # Module configuration
├── gradle.properties                    # Gradle properties
├── .gitignore
├── README.md                            # Main documentation
└── PROJECT_STRUCTURE.md                 # This file
```

## Module Details

### biometric-security-sdk (Library)

**Package**: `com.definex.biometricsdk`

**Key Features**:
- Biometric authentication with sensor-specific enforcement
- Root, Hook, Emulator, and Debug detection
- Hardware-backed AES-GCM encryption
- Secure storage with EncryptedSharedPreferences
- Configurable security policies
- Comprehensive risk reporting

**Dependencies**:
- androidx.biometric:biometric:1.2.0-alpha05
- androidx.security:security-crypto:1.1.0-alpha06
- androidx.core:core-ktx:1.12.0
- kotlinx-coroutines-android:1.7.3

### sample-app (Application)

**Package**: `com.definex.biometricsdk.sample`

**Features**:
- Biometric authentication demo (any, fingerprint, face, iris)
- Device capability detection
- Security risk assessment display
- Security policy configuration UI
- Encrypted storage operations demo

**Dependencies**:
- biometric-security-sdk (module dependency)
- Material Components
- ConstraintLayout
- ViewBinding

## Build Configuration

- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Kotlin**: 1.9.20
- **Gradle**: 8.2
- **Android Gradle Plugin**: 8.2.0
- **JVM Target**: 17

## Getting Started

### Build the Project

```bash
./gradlew build
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

### 1. Biometric Type Enforcement

The SDK enforces specific biometric types by:
1. Checking device capabilities using `PackageManager` features
2. Returning `BiometricNotSupported` error if required type is unavailable
3. NOT showing BiometricPrompt if capability check fails

### 2. Security Policy System

Three-tier policy system:
- **Permissive**: Allow all devices (default)
- **Moderate**: Block rooted and hooked devices
- **Strict**: Block all security risks
- **Custom**: Configure individual restrictions

### 3. Hardware-Backed Encryption

- Uses Android KeyStore for key storage
- Keys are bound to `AUTH_BIOMETRIC_STRONG`
- Keys invalidated on biometric enrollment changes
- AES-256-GCM authenticated encryption

### 4. Security Detection

**Root Detection**:
- su binary in common paths
- Magisk directories
- Dangerous system properties
- Test-keys in build tags

**Hook Detection**:
- XposedBridge class loading
- EdXposed/LSPosed detection
- Riru/Zygisk detection
- Frida server detection
- Suspicious loaded libraries

**Emulator Detection**:
- QEMU kernel property
- Known emulator manufacturers
- Generic device fingerprints
- Suspicious hardware names

**Debug Detection**:
- ApplicationInfo.FLAG_DEBUGGABLE check

## API Usage Examples

### Basic Authentication

```kotlin
val authenticator = BiometricAuthenticator()
authenticator.authenticate(this) { result ->
    when (result) {
        is AuthResult.Success -> println("Success!")
        is AuthResult.Failed -> println("Failed")
        is AuthResult.Error -> println("Error: $result")
    }
}
```

### Enforce Fingerprint

```kotlin
authenticator.authenticate(
    context = this,
    requiredBiometric = BiometricType.FINGERPRINT
) { result ->
    // Handle result
}
```

### Security Policy

```kotlin
authenticator.setSecurityPolicy(SecurityPolicy.strict())
```

### Risk Assessment

```kotlin
val report = authenticator.evaluateRisk(context)
if (report.rooted) {
    println("Device is rooted!")
}
```

### Secure Storage

```kotlin
val storage = authenticator.createSecureStorage(context)
storage.putEncryptedString("key", "value")
val value = storage.getEncryptedString("key")
```

## Testing

The sample app provides comprehensive testing of all SDK features:

1. **Authentication Tests**: Test all biometric types
2. **Capability Tests**: Check available biometrics
3. **Security Tests**: Evaluate device security
4. **Policy Tests**: Configure and test policies
5. **Storage Tests**: Test encrypted storage operations

## Notes

- All code is production-ready with no TODOs or placeholders
- Comprehensive error handling throughout
- Safe logging that respects debug mode
- Clean architecture with separation of concerns
- Well-documented public API
- ProGuard rules included for release builds

## License

Commercial license. Contact vendor for details.

