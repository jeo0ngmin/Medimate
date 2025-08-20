package com.example.medimate.camera

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.medimate.AlarmListActivity
import com.example.medimate.camera.CameraOptionsBottomSheet
import com.example.medimate.databinding.ActivityHomeBinding

// 1. BottomSheet의 리스너를 상속받도록 수정
class CameraActivity : AppCompatActivity(), CameraOptionsBottomSheet.CameraOptionsListener {

    private lateinit var binding: ActivityHomeBinding

    //2. 갤러리와 카메라 결과를 처리할 '런처'들을 등록

    // 갤러리 런처
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                // 이미지가 성공적으로 선택됨
                // TODO: 여기서 OCR 처리 화면으로 uri를 전달합니다.
                Toast.makeText(this, "이미지 선택 완료: $uri", Toast.LENGTH_SHORT).show()
            }
        }

    // 카메라 런처 (단순 실행)
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                // 사진이 성공적으로 촬영됨
                // TODO: 여기서 OCR 처리 화면으로 bitmap을 전달합니다.
                Toast.makeText(this, "사진 촬영 완료!", Toast.LENGTH_SHORT).show()
            }
        }

    // 권한 요청 런처
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용되면 카메라 실행
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // (기존 코드)
        binding.btnAlarm.setOnClickListener {
            startActivity(Intent(this, AlarmListActivity::class.java))
        }

        // --- ▼▼ 3. BottomSheet를 생성하고 리스너를 연결 ▼▼ ---
        binding.btnCamera.setOnClickListener {
            val bottomSheet = CameraOptionsBottomSheet()
            bottomSheet.setListener(this) // "내가 너의 신호를 받을게" 라고 설정
            bottomSheet.show(supportFragmentManager, CameraOptionsBottomSheet.TAG)
        }
    }

    // --- ▼▼ 4. BottomSheet의 신호를 받았을 때 실행될 함수들 구현 ▼▼ ---

    // '사진 촬영' 버튼이 눌렸을 때
    override fun onTakePhotoClicked() {
        // 카메라 권한을 확인하고, 없으면 요청, 있으면 바로 실행
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // '앨범에서 선택' 버튼이 눌렸을 때
    override fun onOpenGalleryClicked() {
        // 갤러리 런처 실행 (모든 이미지 타입)
        galleryLauncher.launch("image/*")
    }
}