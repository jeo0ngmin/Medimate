package com.example.medimate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medimate.alarm.Alarm
import com.example.medimate.alarm.AlarmAdapter
import com.example.medimate.databinding.ActivityAlarmListBinding

class AlarmListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 오른쪽 위 '+' 버튼 클릭 리스너
        binding.ivAddAlarm.setOnClickListener {
            val intent = Intent(this, AlarmSettingActivity::class.java)
            startActivity(intent)
        }


        // 샘플 데이터 (이 부분은 기존과 동일합니다)
        val sampleAlarms = listOf(
            Alarm(
                time = "오전 9:00",
                daysSelected = BooleanArray(7) { true }, // 매일
                isEnabled = true
            ),
            Alarm(
                time = "오후 1:00",
                daysSelected = booleanArrayOf(false, true, true, true, true, true, false), // 월~금
                isEnabled = true
            ),
            Alarm(
                time = "오후 7:00",
                daysSelected = booleanArrayOf(true, false, false, false, false, false, true), // 주말(일,토)
                isEnabled = false
            )
        )

        // RecyclerView 설정 (이 부분은 기존과 동일합니다)
        val alarmAdapter = AlarmAdapter(sampleAlarms)
        binding.rvAlarms.adapter = alarmAdapter
        binding.rvAlarms.layoutManager = LinearLayoutManager(this)
    }
}