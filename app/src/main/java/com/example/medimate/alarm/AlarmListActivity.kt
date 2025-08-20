package com.example.medimate.alarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medimate.databinding.ActivityAlarmListBinding

class AlarmListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        // 새로운 데이터 구조에 맞는 샘플 데이터 생성
        // BooleanArray(7) { true } -> 모든 요일이 true인 배열
        // booleanArrayOf(false, true, true, true, true, true, false) -> 월~금만 true
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

        // 어댑터를 생성하고 RecyclerView에 연결
        val alarmAdapter = AlarmAdapter(sampleAlarms)
        binding.rvAlarms.adapter = alarmAdapter
        binding.rvAlarms.layoutManager = LinearLayoutManager(this)
    }
}