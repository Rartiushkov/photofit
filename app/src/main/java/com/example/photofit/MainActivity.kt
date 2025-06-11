package com.example.photofit

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { analyzeImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.select_button).setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun analyzeImage(uri: Uri) {
        val file = File(cacheDir, "selected.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val result = runCatching { SpoonacularApi.analyzePhoto(file) }
                .getOrElse { it.message ?: "Error" }
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.result_text).text = result
            }
        }

    }
}
