package com.example.medimate.recommendation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medimate.R

class HealthInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //1. 이 화면의 UI를 activity_health_input으로 지정
        setContentView(R.layout.activity_health_input)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.input)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //2. xml에 있는 checkbox와 button을 변수에 연결
        val fatigueCheckBox = findViewById<CheckBox>(R.id.cb_fatigue)
        val sleepCheckBox = findViewById<CheckBox>(R.id.cb_sleep)
        val digestionCheckBox = findViewById<CheckBox>(R.id.cb_digestion)
        val immunityCheckBox = findViewById<CheckBox>(R.id.cb_immunity)
        val focusCheckBox = findViewById<CheckBox>(R.id.cb_focus)
        val submitButton = findViewById<Button>(R.id.btn_submit)

        //3. 추천받기 버튼 클릭됐을 때 동작
        submitButton.setOnClickListener {
            val selectedSymptoms = ArrayList<String>()

            //4. 각 체크박스가 선택되었는지 확인하고 리스트에 키워드 추가
            if (fatigueCheckBox.isChecked) {
                selectedSymptoms.add("피로")
            }
            if (sleepCheckBox.isChecked) {
                selectedSymptoms.add("수면")
            }
            if (digestionCheckBox.isChecked) {
                selectedSymptoms.add("장")
                selectedSymptoms.add("배변")
            }
            if (immunityCheckBox.isChecked) {
                selectedSymptoms.add("면역")
            }
            if (focusCheckBox.isChecked) {
                selectedSymptoms.add("기억력")
            }

            //5. 키워드가 하나라도 리스트에 추가되었다면
            if (selectedSymptoms.isNotEmpty()) {
                //6. RecommendActivity로 이동
                val intent = Intent(this, RecommendActivity::class.java)
                intent.putStringArrayListExtra("health_status", selectedSymptoms)
                startActivity(intent)
            } else {
                //아무것도 선택 안했을 때
                Toast.makeText(this, "하나 이상의 건강 상태를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}