package com.example.medimate

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.medimate.alarm.AlarmListActivity
import com.example.medimate.camera.CameraOptionsBottomSheet
import com.example.medimate.databinding.ActivityHomeBinding
import com.example.medimate.recommendation.HealthInputActivity

// 카메라/갤러리 선택 BottomSheet의 버튼 클릭 신호를 받기 위해 리스너를 상속받습니다.
class HomeActivity : AppCompatActivity(), CameraOptionsBottomSheet.CameraOptionsListener {

    // viewBinding을 사용해 XML의 뷰(버튼 등)에 쉽게 접근합니다.
    private lateinit var binding: ActivityHomeBinding

    // 갤러리 앱을 실행하고 결과를 받아 처리하는 런처입니다.
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                Toast.makeText(this, "이미지를 선택했습니다.", Toast.LENGTH_SHORT).show()
                // 예: val intent = Intent(this, OcrActivity::class.java).apply { data = uri }
                //     startActivity(intent)
            }
        }

    // 카메라 앱을 실행하고 결과를 받아 처리하는 런처입니다.
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                // TODO: 사진이 촬영되면 OCR 처리 화면으로 bitmap을 전달해야 합니다.
                Toast.makeText(this, "사진을 촬영했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 카메라 권한을 요청하고 결과를 받아 처리하는 런처입니다.
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용되면 카메라를 실행합니다.
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. viewBinding을 초기화하고 화면으로 설정합니다.
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)


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

        // '약 봉투 찍기' 버튼 클릭 시
        binding.btnCamera.setOnClickListener {
            // 카메라/갤러리 선택 옵션을 보여주는 BottomSheet를 띄웁니다.
            val bottomSheet = CameraOptionsBottomSheet()
            bottomSheet.setListener(this) // BottomSheet에서 버튼이 눌리면 이 Activity에 알려달라고 설정
            bottomSheet.show(supportFragmentManager, CameraOptionsBottomSheet.TAG)
        }
    }

    // --- BottomSheet 리스너 구현 부분 ---

    // BottomSheet에서 '사진 촬영' 버튼을 눌렀을 때 호출됩니다.
    override fun onTakePhotoClicked() {
        // 카메라 권한을 확인하고, 없으면 요청, 있으면 바로 카메라를 실행합니다.
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // BottomSheet에서 '앨범에서 선택' 버튼을 눌렀을 때 호출됩니다.
    override fun onOpenGalleryClicked() {
        // 갤러리를 실행합니다.
        galleryLauncher.launch("image/*")
    }
}