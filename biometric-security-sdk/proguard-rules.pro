# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep SDK public API
-keep public class com.definex.biometricsdk.auth.BiometricAuthenticator { *; }
-keep public class com.definex.biometricsdk.model.** { *; }

# Keep encryption classes
-keep class com.definex.biometricsdk.crypto.** { *; }

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

