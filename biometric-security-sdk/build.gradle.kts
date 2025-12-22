plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.definex.biometricsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 28
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Biometric
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    
    // Security Crypto
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// JitPack Maven Publish Configuration
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                
                groupId = "com.github.Definex-Mobile"
                artifactId = "biometric-security-sdk"
                version = "1.0.1"
                
                pom {
                    name.set("Biometric Security SDK")
                    description.set("Android biometric authentication SDK with security policy enforcement")
                    url.set("https://github.com/Definex-Mobile/Android-Biometric-SDK")
                    
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("definex-mobile")
                            name.set("Definex Mobile Team")
                            email.set("mobile@definex.com")
                        }
                    }
                    
                    scm {
                        connection.set("scm:git:github.com/Definex-Mobile/Android-Biometric-SDK.git")
                        developerConnection.set("scm:git:ssh://github.com/Definex-Mobile/Android-Biometric-SDK.git")
                        url.set("https://github.com/Definex-Mobile/Android-Biometric-SDK")
                    }
                }
            }
        }
    }
}
