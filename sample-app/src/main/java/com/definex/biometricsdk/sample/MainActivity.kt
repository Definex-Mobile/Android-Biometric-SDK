package com.definex.biometricsdk.sample

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Sample app - Simple starter version
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var btnTest: Button
    private lateinit var tvMessage: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupListeners()
    }
    
    private fun initializeViews() {
        btnTest = findViewById(R.id.btnTest)
        tvMessage = findViewById(R.id.tvMessage)
    }
    
    private fun setupListeners() {
        btnTest.setOnClickListener {
            tvMessage.text = "Sample App is working! ✓"
            Toast.makeText(this, "Hello from Sample App!", Toast.LENGTH_SHORT).show()
        }
    }
}
