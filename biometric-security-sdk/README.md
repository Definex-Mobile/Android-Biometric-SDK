# Biometric Security SDK

A commercial-grade Android library for biometric authentication with comprehensive security features.

## Features

- ✅ Biometric authentication with sensor-specific enforcement (Fingerprint, Face, Iris)
- ✅ Root detection (su, Magisk, dangerous properties)
- ✅ Hook/Framework detection (Xposed, Frida, Riru/Zygisk)
- ✅ Emulator detection
- ✅ Debug mode detection
- ✅ Hardware-backed AES-GCM encryption
- ✅ Secure storage with EncryptedSharedPreferences
- ✅ Configurable security policies
- ✅ Comprehensive risk reporting

## Quick Start

```kotlin
val authenticator = BiometricAuthenticator()

// Set security policy
authenticator.setSecurityPolicy(SecurityPolicy.moderate())

// Authenticate
authenticator.authenticate(
    context = this,
    requiredBiometric = BiometricType.FINGERPRINT
) { result ->
    when (result) {
        is AuthResult.Success -> println("Authenticated!")
        is AuthResult.Failed -> println("Failed")
        is AuthResult.Error -> println("Error: $result")
    }
}
```

## Integration

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":biometric-security-sdk"))
}
```

Add permission to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
```

## Documentation

See the main [README.md](../README.md) in the project root for complete documentation.

## Package Structure

```
com.definex.biometricsdk
├── auth/              # Biometric authentication
├── crypto/            # Encryption and key management
├── security/          # Security detectors
├── model/             # Data models
└── util/              # Utilities
```

## Requirements

- Min SDK: 28 (Android 9.0)
- Target SDK: 34 (Android 14)
- Kotlin 1.9.20+

## License

Commercial license. Contact vendor for details.

