package com.definex.biometricsdk.sample

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.definex.biometricsdk.auth.BiometricAuthenticator
import com.definex.biometricsdk.crypto.SecureStorage
import com.definex.biometricsdk.model.AuthResult
import com.definex.biometricsdk.model.BiometricType
import com.definex.biometricsdk.model.SecurityPolicy
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

/**
 * Sample app demonstrating the Biometric Security SDK capabilities.
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var biometricAuthenticator: BiometricAuthenticator
    private lateinit var secureStorage: SecureStorage
    
    // UI Components
    private lateinit var btnAuthenticate: Button
    private lateinit var btnAuthenticateFingerprint: Button
    private lateinit var btnAuthenticateFace: Button
    private lateinit var btnAuthenticateIris: Button
    private lateinit var tvAuthResult: TextView
    
    private lateinit var btnCheckCapabilities: Button
    private lateinit var tvCapabilities: TextView
    
    private lateinit var btnEvaluateRisk: Button
    private lateinit var tvRiskReport: TextView
    
    private lateinit var switchRooted: SwitchMaterial
    private lateinit var switchEmulator: SwitchMaterial
    private lateinit var switchHooked: SwitchMaterial
    private lateinit var switchDebug: SwitchMaterial
    private lateinit var btnApplyPolicy: Button
    
    private lateinit var etStorageKey: TextInputEditText
    private lateinit var etStorageValue: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnLoad: Button
    private lateinit var btnClearStorage: Button
    private lateinit var tvStorageResult: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize SDK
        biometricAuthenticator = BiometricAuthenticator()
        biometricAuthenticator.setDebugLogging(true)
        secureStorage = biometricAuthenticator.createSecureStorage(this)
        
        // Initialize UI
        initializeViews()
        setupListeners()
    }
    
    private fun initializeViews() {
        // Authentication
        btnAuthenticate = findViewById(R.id.btnAuthenticate)
        btnAuthenticateFingerprint = findViewById(R.id.btnAuthenticateFingerprint)
        btnAuthenticateFace = findViewById(R.id.btnAuthenticateFace)
        btnAuthenticateIris = findViewById(R.id.btnAuthenticateIris)
        tvAuthResult = findViewById(R.id.tvAuthResult)
        
        // Capabilities
        btnCheckCapabilities = findViewById(R.id.btnCheckCapabilities)
        tvCapabilities = findViewById(R.id.tvCapabilities)
        
        // Security
        btnEvaluateRisk = findViewById(R.id.btnEvaluateRisk)
        tvRiskReport = findViewById(R.id.tvRiskReport)
        
        // Policy
        switchRooted = findViewById(R.id.switchRooted)
        switchEmulator = findViewById(R.id.switchEmulator)
        switchHooked = findViewById(R.id.switchHooked)
        switchDebug = findViewById(R.id.switchDebug)
        btnApplyPolicy = findViewById(R.id.btnApplyPolicy)
        
        // Storage
        etStorageKey = findViewById(R.id.etStorageKey)
        etStorageValue = findViewById(R.id.etStorageValue)
        btnSave = findViewById(R.id.btnSave)
        btnLoad = findViewById(R.id.btnLoad)
        btnClearStorage = findViewById(R.id.btnClearStorage)
        tvStorageResult = findViewById(R.id.tvStorageResult)
    }
    
    private fun setupListeners() {
        // Authentication buttons
        btnAuthenticate.setOnClickListener {
            authenticate(null)
        }
        
        btnAuthenticateFingerprint.setOnClickListener {
            authenticate(BiometricType.FINGERPRINT)
        }
        
        btnAuthenticateFace.setOnClickListener {
            authenticate(BiometricType.FACE)
        }
        
        btnAuthenticateIris.setOnClickListener {
            authenticate(BiometricType.IRIS)
        }
        
        // Capabilities
        btnCheckCapabilities.setOnClickListener {
            checkCapabilities()
        }
        
        // Security
        btnEvaluateRisk.setOnClickListener {
            evaluateRisk()
        }
        
        // Policy
        btnApplyPolicy.setOnClickListener {
            applySecurityPolicy()
        }
        
        // Storage
        btnSave.setOnClickListener {
            saveToStorage()
        }
        
        btnLoad.setOnClickListener {
            loadFromStorage()
        }
        
        btnClearStorage.setOnClickListener {
            clearStorage()
        }
    }
    
    private fun authenticate(requiredBiometric: BiometricType?) {
        val biometricName = requiredBiometric?.name ?: "Any"
        tvAuthResult.text = "Authenticating with $biometricName..."
        tvAuthResult.setTextColor(Color.BLUE)
        
        biometricAuthenticator.authenticate(
            context = this,
            requiredBiometric = requiredBiometric,
            challenge = null
        ) { result ->
            handleAuthResult(result)
        }
    }
    
    private fun handleAuthResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> {
                tvAuthResult.text = "✓ Authentication Successful!"
                tvAuthResult.setTextColor(Color.GREEN)
                Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
            }
            
            is AuthResult.Failed -> {
                tvAuthResult.text = "✗ Authentication Failed - Biometric not recognized"
                tvAuthResult.setTextColor(Color.RED)
            }
            
            is AuthResult.Error.BiometricNotSupported -> {
                tvAuthResult.text = "✗ Error: ${result.type.name} biometric not supported on this device"
                tvAuthResult.setTextColor(Color.RED)
                Toast.makeText(
                    this,
                    "${result.type.name} not available",
                    Toast.LENGTH_LONG
                ).show()
            }
            
            is AuthResult.Error.SecurityViolation -> {
                val violations = result.report.getDetectedRisks().joinToString("\n• ")
                tvAuthResult.text = "✗ Security Violation Detected:\n• $violations"
                tvAuthResult.setTextColor(Color.RED)
                Toast.makeText(
                    this,
                    "Security policy violation",
                    Toast.LENGTH_LONG
                ).show()
            }
            
            is AuthResult.Error.AuthenticationError -> {
                tvAuthResult.text = "✗ Error: ${result.errorMessage} (Code: ${result.errorCode})"
                tvAuthResult.setTextColor(Color.RED)
            }
        }
    }
    
    private fun checkCapabilities() {
        val availableBiometrics = biometricAuthenticator.getAvailableBiometrics(this)
        
        if (availableBiometrics.isEmpty()) {
            tvCapabilities.text = "No biometric authentication available on this device"
            tvCapabilities.setTextColor(Color.RED)
        } else {
            val biometricsList = availableBiometrics.joinToString("\n• ") { it.name }
            tvCapabilities.text = "Available:\n• $biometricsList"
            tvCapabilities.setTextColor(Color.GREEN)
        }
    }
    
    private fun evaluateRisk() {
        val riskReport = biometricAuthenticator.evaluateRisk(this)
        
        val reportText = buildString {
            append("Rooted: ${if (riskReport.rooted) "✗ YES" else "✓ NO"}\n")
            append("Emulator: ${if (riskReport.emulator) "✗ YES" else "✓ NO"}\n")
            append("Hooked: ${if (riskReport.hookingDetected) "✗ YES" else "✓ NO"}\n")
            append("Debug: ${if (riskReport.debugMode) "✗ YES" else "✓ NO"}\n\n")
            
            if (riskReport.hasAnyRisk()) {
                append("⚠ Risks detected:\n")
                riskReport.getDetectedRisks().forEach { risk ->
                    append("• $risk\n")
                }
            } else {
                append("✓ No security risks detected")
            }
        }
        
        tvRiskReport.text = reportText
        tvRiskReport.setTextColor(if (riskReport.hasAnyRisk()) Color.RED else Color.GREEN)
    }
    
    private fun applySecurityPolicy() {
        val policy = SecurityPolicy(
            disallowRootedDevices = switchRooted.isChecked,
            disallowEmulators = switchEmulator.isChecked,
            disallowHookedDevices = switchHooked.isChecked,
            disallowDebuggableApps = switchDebug.isChecked
        )
        
        biometricAuthenticator.setSecurityPolicy(policy)
        
        Toast.makeText(
            this,
            "Security policy applied successfully",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun saveToStorage() {
        val key = etStorageKey.text?.toString()
        val value = etStorageValue.text?.toString()
        
        if (key.isNullOrBlank() || value.isNullOrBlank()) {
            Toast.makeText(this, "Please enter both key and value", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            secureStorage.putEncryptedString(key, value)
            tvStorageResult.text = "✓ Saved encrypted: '$key' = '$value'"
            tvStorageResult.setTextColor(Color.GREEN)
            Toast.makeText(this, "Data saved securely", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            tvStorageResult.text = "✗ Error saving: ${e.message}"
            tvStorageResult.setTextColor(Color.RED)
        }
    }
    
    private fun loadFromStorage() {
        val key = etStorageKey.text?.toString()
        
        if (key.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a key", Toast.LENGTH_SHORT).show()
            return
        }
        
        try {
            val value = secureStorage.getEncryptedString(key)
            if (value != null) {
                etStorageValue.setText(value)
                tvStorageResult.text = "✓ Loaded encrypted value for key '$key'"
                tvStorageResult.setTextColor(Color.GREEN)
                Toast.makeText(this, "Data loaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                tvStorageResult.text = "✗ No value found for key '$key'"
                tvStorageResult.setTextColor(Color.RED)
            }
        } catch (e: Exception) {
            tvStorageResult.text = "✗ Error loading: ${e.message}"
            tvStorageResult.setTextColor(Color.RED)
        }
    }
    
    private fun clearStorage() {
        try {
            secureStorage.clear()
            etStorageKey.text?.clear()
            etStorageValue.text?.clear()
            tvStorageResult.text = "✓ All encrypted storage cleared"
            tvStorageResult.setTextColor(Color.GREEN)
            Toast.makeText(this, "Storage cleared", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            tvStorageResult.text = "✗ Error clearing storage: ${e.message}"
            tvStorageResult.setTextColor(Color.RED)
        }
    }
}

