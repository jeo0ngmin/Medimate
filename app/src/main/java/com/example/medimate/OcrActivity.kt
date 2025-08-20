package com.example.medimate

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

class MainActivity : ComponentActivity() {

    private lateinit var preview: ImageView
    private lateinit var tvResult: TextView

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}

            showPreview(it)
            runOcr(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        preview = findViewById(R.id.preview)
        tvResult = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.btnPick).setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }
    }

    private fun showPreview(uri: Uri) {
        try {
            val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
            preview.setImageBitmap(bmp)
        } catch (e: Exception) {
            tvResult.text = "미리보기 실패: ${e.message}"
        }
    }

    private fun runOcr(uri: Uri) {
        tvResult.text = "인식 중…"
        val image = InputImage.fromFilePath(this, uri)
        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text.trim()
                tvResult.text = if (text.isNotEmpty()) text else "텍스트를 찾지 못함"
            }
            .addOnFailureListener { e ->
                tvResult.text = "인식 실패: ${e.message}"
            }
    }
}
