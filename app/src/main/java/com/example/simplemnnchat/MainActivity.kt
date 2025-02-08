package com.example.simplemnnchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var chatSession: ChatSession
    private lateinit var inputText: EditText
    private lateinit var sendButton: Button
    private lateinit var responseText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputText = findViewById(R.id.inputText)
        sendButton = findViewById(R.id.sendButton)
        responseText = findViewById(R.id.responseText)

        chatSession = ChatSession()
        val configPath = setupModel()
        if (!chatSession.create(configPath)) {
            Toast.makeText(this, "Failed to initialize model", Toast.LENGTH_SHORT).show()
            return
        }

        sendButton.setOnClickListener {
            val input = inputText.text.toString()
            if (input.isNotEmpty()) {
                generateResponse(input)
            }
        }
    }

    private fun generateResponse(input: String) {
        Thread {
            val result = chatSession.generate(input, object : GenerateProgressListener {
                override fun onProgress(progress: String): Boolean {
                    runOnUiThread {
                        responseText.text = progress
                    }
                    return false
                }
            })
            
            // Handle final result if needed
            runOnUiThread {
                inputText.text.clear()
            }
        }.start()
    }

    private fun setupModel(): String {
        val modelDir = File(filesDir, "models/Qwen-2-VL-2B-Instruct-MNN")
        if (!modelDir.exists()) {
            modelDir.mkdirs()
            // Copy assets to internal storage
            assets.open("models/Qwen-2-VL-2B-Instruct-MNN/config.json").use { input ->
                File(modelDir, "config.json").outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            // Copy model file
            assets.open("models/Qwen-2-VL-2B-Instruct-MNN/model.mnn").use { input ->
                File(modelDir, "model.mnn").outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        return "${modelDir.absolutePath}/config.json"
    }

    override fun onDestroy() {
        super.onDestroy()
        chatSession.release()
    }
} 