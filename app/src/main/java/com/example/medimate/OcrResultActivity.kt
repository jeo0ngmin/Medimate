package com.example.medimate // 실제 패키지명으로 변경

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity



class OcrResultActivity : AppCompatActivity() {

    private lateinit var tvOcrResult: TextView

    companion object {
        const val EXTRA_OCR_TEXT = "com.example.medimate.EXTRA_OCR_TEXT" // Intent의 Key 값
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr_result) // 위에서 만든 레이아웃 파일

        tvOcrResult = findViewById(R.id.tvOcrResult)

        val recognizedText = intent.getStringExtra(EXTRA_OCR_TEXT)

        if (recognizedText != null) {
            tvOcrResult.text = recognizedText
        } else {
            tvOcrResult.text = "인식된 텍스트가 없습니다."
        }
    }
}