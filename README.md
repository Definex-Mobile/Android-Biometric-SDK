# Sample App

A simple Android sample application.

## Getting Started

### Build the Project

```bash
./gradlew build
```

### Run Sample App

```bash
./gradlew :sample-app:installDebug
```

## Project Structure

```
Android-Biometric-SDK/
├── sample-app/              # Sample Application
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/definex/biometricsdk/sample/
│   │   │   └── MainActivity.kt
│   │   └── res/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## Requirements

- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.20+
- **Gradle**: 8.4

