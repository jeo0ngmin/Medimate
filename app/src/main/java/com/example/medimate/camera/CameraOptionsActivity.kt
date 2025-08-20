package com.example.medimate.camera // 실제 패키지명으로 변경

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.medimate.OcrResultActivity // 결과를 보여줄 Activity
import com.example.medimate.R // 실제 R 파일 경로로
import com.example.medimate.databinding.ActivityCameraOptionsBinding // 변경된 레이아웃 바인딩 클래스
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.IOException

class CameraOptionsActivity : AppCompatActivity() { // AppCompatActivity 상속

    private lateinit var binding: ActivityCameraOptionsBinding // 변경된 바인딩 클래스 이름
    private var imageUri: Uri? = null
    private val textRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var openGalleryLauncher: ActivityResultLauncher<String> // 갤러리 기능도 남겨둠

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraOptionsBinding.inflate(layoutInflater) // 바인딩 클래스 사용
        setContentView(binding.root)

        // ActivityResultLauncher 초기화
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    launchCamera()
                } else {
                    Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                    // 권한 거부 시 현재 Activity를 닫거나 다른 UI 처리
                    finish()
                }
            }

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
                if (success) {
                    imageUri?.let { uri ->
                        recognizeTextFromImage(uri)
                    } ?: run {
                        Toast.makeText(this, "이미지 URI가 없습니다.", Toast.LENGTH_SHORT).show()
                        finish() // 오류 시 닫기
                    }
                } else {
                    Toast.makeText(this, "사진 촬영에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    finish() // 실패 시 닫기
                }
            }

        openGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    recognizeTextFromImage(it)
                } ?: run {
                    Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
                    // 갤러리 선택 안했을 때 현재 Activity를 닫을 필요는 없을 수 있음
                }
            }

        // 버튼 클릭 리스너 설정
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        binding.btnOpenGallery.setOnClickListener { // 갤러리 버튼도 남겨둠
            openGalleryLauncher.launch("image/*")
        }

        // 닫기 버튼 (예: 제목 표시줄의 뒤로가기 버튼이나, 레이아웃에 직접 추가한 버튼)
        // 만약 레이아웃에 btnCloseSheet 같은 ID의 버튼이 있다면:
        binding.btnCloseSheet.setOnClickListener {
             finish()
        }
        // 또는 ActionBar의 뒤로가기 버튼을 활성화하려면 테마 및 코드 설정 필요
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // ActionBar 뒤로가기 버튼 (테마에서 ActionBar가 활성화 되어있어야 함)
    }

    // ActionBar 뒤로가기 버튼 클릭 처리 (선택 사항)
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(this, "사진 촬영을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                // 권한 요청 다이얼로그 등을 직접 띄워줄 수도 있습니다.
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        imageUri = createImageUri()
        imageUri?.let {
            takePictureLauncher.launch(it)
        } ?: run {
            Toast.makeText(this, "이미지를 저장할 URI 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
            finish() // URI 생성 실패 시 닫기
        }
    }

    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "ocr_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Medimate") // 앱별 저장소 권장
            }
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private fun recognizeTextFromImage(uri: Uri) {
        try {
            // 로딩 UI 표시 (선택 사항: binding.progressBar.visibility = View.VISIBLE 등)
            val inputImage = InputImage.fromFilePath(this, uri)
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    // 로딩 UI 숨김
                    val resultText = visionText.text.trim()
                    Log.d(TAG, "인식된 텍스트: $resultText")

                    if (resultText.isNotEmpty()) {
                        val intent = Intent(this, OcrResultActivity::class.java).apply {
                            putExtra(OcrResultActivity.EXTRA_OCR_TEXT, resultText)
                        }
                        startActivity(intent)
                        finish() // OCR 결과 화면으로 이동 후 현재 Activity는 닫기
                    } else {
                        Toast.makeText(this, "텍스트를 찾지 못했습니다.", Toast.LENGTH_SHORT).show()
                        // 텍스트 못찾았을 때 현재 Activity를 닫을지 사용자가 다시 시도하게 할지 결정
                    }
                }
                .addOnFailureListener { e ->
                    // 로딩 UI 숨김
                    Log.e(TAG, "텍스트 인식 실패: ${e.message}", e)
                    Toast.makeText(this, "텍스트 인식에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                    // 오류 발생 시 현재 Activity를 닫을지 사용자가 다시 시도하게 할지 결정
                }
        } catch (e: IOException) {
            // 로딩 UI 숨김
            Log.e(TAG, "InputImage 생성 실패: ${e.message}", e)
            Toast.makeText(this, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // 리소스 해제는 Activity 생명주기에 따라 관리될 수 있음
    // override fun onDestroy() {
    //     super.onDestroy()
    //     // textRecognizer.close() // 필요시 명시적 해제
    // }

    companion object {
        private const val TAG = "CameraOptionsActivity"
    }
}
   