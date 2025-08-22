package com.example.medimate

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medimate.tts.OcrParser
import com.example.medimate.tts.TTSSpeechBuilder
import com.example.medimate.tts.TTSApi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.IOException

class OcrActivity : AppCompatActivity() {

    private lateinit var ivPreview: ImageView
    private lateinit var tvResult: TextView
    private lateinit var btnSpeak: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ocr)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ocr)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivPreview = findViewById(R.id.iv_preview)
        tvResult = findViewById(R.id.tv_result)
        btnSpeak = findViewById(R.id.btn_speak)

        val imageUri = intent.data

        if (imageUri != null) {
            ivPreview.setImageURI(imageUri)
            runOcr(imageUri)
        } else {
            Toast.makeText(this, "이미지를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun runOcr(uri: Uri) {
        tvResult.text = "텍스트를 인식하는 중입니다..."
        try {
            val image = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val rawText = visionText.text
                    if (rawText.isNotBlank()) {
                        // 1. OcrParser를 직접 호출합니다.
                        val parsedResult = OcrParser.parse(rawText)

                        // 2. 분석 결과를 화면에 보기 좋게 표시합니다. (단순 텍스트 대신)
                        val displayText = """
                            - 약품명: ${parsedResult.drugNames.joinToString(", ")}
                            - 복용 횟수: ${parsedResult.timesPerDay ?: "정보 없음"}
                            - 복용 시간: ${parsedResult.mealHints.joinToString(", ")}
                            - 1회 용량: ${parsedResult.doseText ?: "정보 없음"}
                            - 총 복용일: ${parsedResult.totalDays ?: "정보 없음"}
                            
                            - 주의사항:
                            ${parsedResult.cautions.joinToString("\n")}
                        """.trimIndent()
                        tvResult.text = displayText

                        // 3. 새로워진 TTSSpeechBuilder를 사용합니다.
                        val speechLines = TTSSpeechBuilder.toSpeechLines(parsedResult)

                        btnSpeak.setOnClickListener {
                            TTSApi.init(this)
                            speechLines.forEach { line ->
                                TTSApi.speakAdd(line)
                                TTSApi.pause(200) // 문장 사이에 약간의 쉼을 줍니다.
                            }
                        }
                    } else {
                        tvResult.text = "이미지에서 텍스트를 찾지 못했습니다."
                    }
                }
                .addOnFailureListener { e ->
                    tvResult.text = "텍스트 인식에 실패했습니다: ${e.message}"
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}