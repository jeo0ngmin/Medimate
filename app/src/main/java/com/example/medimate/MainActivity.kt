// MainActivity.kt 파일의 전체 내용

package com.example.medimate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.example.medimate.tts.OcrParser
import com.example.medimate.tts.TTSSpeechBuilder
import com.example.medimate.tts.TTSApi
import com.example.medimate.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. 시작 화면으로 사용할 XML 레이아웃을 설정합니다.
        setContentView(R.layout.activity_main)

        // 2. 3초 딜레이 후 코드를 실행합니다.
        Handler(Looper.getMainLooper()).postDelayed({

            // 3초 뒤에 실행될 내용
            // HomeActivity로 이동하기 위한 "티켓"을 만듭니다.
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

            // 3. 현재 화면(MainActivity)을 종료합니다.
            // (이걸 해야 홈 화면에서 뒤로가기 했을 때 이 화면이 다시 안 나옵니다)
            finish()

        }, 3000) // 딜레이 시간 (3000ms = 3초)


    }
}