package com.example.medimate

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medimate.alarm.AlarmListActivity
import com.example.medimate.camera.CameraOptionsActivity
import com.example.medimate.databinding.ActivityHomeBinding
import com.example.medimate.recommendation.HealthInputActivity

// 카메라/갤러리 선택 BottomSheet의 버튼 클릭 신호를 받기 위해 리스너를 상속받습니다.
class HomeActivity : AppCompatActivity() {

    // viewBinding을 사용해 XML의 뷰(버튼 등)에 쉽게 접근합니다.
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. viewBinding을 초기화하고 화면으로 설정합니다.
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_home)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener { // activity_home.xml의 버튼 ID가 btnCamera라고 가정
            val intent = Intent(this, CameraOptionsActivity::class.java)
            startActivity(intent)
        }

        // 2. 각 버튼에 클릭 리스너를 설정하는 함수를 호출합니다.
        setupClickListeners()
    }

    /**
     * 화면에 있는 각 버튼을 눌렀을 때 어떤 동작을 할지 설정합니다.
     */
    private fun setupClickListeners() {
        // '알림' 버튼 클릭 시
        binding.btnAlarm.setOnClickListener {
            // AlarmListActivity로 이동하는 Intent(티켓) 생성
            val intent = Intent(this, AlarmListActivity::class.java)
            startActivity(intent)
        }

        // '복용 내역' 버튼 클릭 시
        binding.btnHistory.setOnClickListener {
            // 아직 기능이 없으므로 간단한 안내 메시지(Toast)를 띄웁니다.
            Toast.makeText(this, "복용 내역 기능은 아직 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }

        // '영양제 추천' 버튼 클릭 시
        binding.btnPill.setOnClickListener {
            // HealthInputActivity로 이동하는 Intent 생성
            val intent = Intent(this, HealthInputActivity::class.java)
            startActivity(intent)
        }


    }
}