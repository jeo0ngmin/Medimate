// HomeActivity.kt
package com.example.medimate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medimate.camera.CameraOptionsActivity
import com.example.medimate.databinding.ActivityHomeBinding


// 1. BottomSheet의 리스너를 상속받도록 수정
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener { // activity_home.xml의 버튼 ID가 btnCamera라고 가정
            val intent = Intent(this, CameraOptionsActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
}