package com.example.medimate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.medimate.camera.CameraOptionsActivity // CameraOptionsActivity 경로 확인
import com.example.medimate.databinding.ActivityHomeBinding // ViewBinding 클래스

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // '카메라 옵션 화면으로 이동' 버튼 클릭 리스너
        // activity_home.xml 파일에 있는 버튼의 ID가 btnCamera라고 가정합니다.
        binding.btnCamera.setOnClickListener {
            val intent = Intent(this, CameraOptionsActivity::class.java)
            startActivity(intent)
        }

        // '알람 목록 화면으로 이동' 버튼 클릭 리스너 (선택 사항 - 기존 코드에 있었다면 유지)
        // activity_home.xml 파일에 있는 버튼의 ID가 btnAlarm이라고 가정합니다.
        // 만약 이 버튼이 없다면 이 부분은 삭제해도 됩니다.
        binding.btnAlarm?.setOnClickListener { // btnAlarm이 레이아웃에 없을 수도 있으므로 안전 호출 사용
            val intent = Intent(this, AlarmListActivity::class.java) // AlarmListActivity 경로 확인
            startActivity(intent)
        }

        // 여기에 HomeActivity의 다른 초기화 로직이나 UI 이벤트 핸들러를 추가할 수 있습니다.
    }
}
