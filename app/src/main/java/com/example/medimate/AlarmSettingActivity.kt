package com.example.medimate

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.medimate.databinding.ActivityAlarmSettingBinding
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class AlarmSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmSettingBinding
    private var alarmHour: Int = 0
    private var alarmMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ▼▼ '닫기' 버튼 텍스트 수정 ▼▼
        binding.btnCancel.text = "닫기"

        // 알림 목록에서 전달받은 기존 알람 데이터가 있는지 확인
        val existingTime = intent.getStringExtra("EXISTING_ALARM_TIME")
        val existingDays = intent.getBooleanArrayExtra("EXISTING_ALARM_DAYS_BOOLEAN")

        if (existingTime != null && existingDays != null) {
            // 전달받은 데이터로 UI 설정
            // TODO: "오전 9:00" 같은 문자열을 파싱해서 alarmHour, alarmMinute에 설정해야 함
            updateUITime(alarmHour, alarmMinute) // 임시로 현재 시간 표시
            binding.tvTimeValue.text = existingTime
            updateDaysUI(existingDays)
        } else {
            // 새 알람 설정 시: 현재 시간으로 초기화
            val calendar = Calendar.getInstance()
            alarmHour = calendar.get(Calendar.HOUR_OF_DAY)
            alarmMinute = calendar.get(Calendar.MINUTE)
            updateUITime(alarmHour, alarmMinute)
        }

        binding.tvTimeValue.setOnClickListener { showTimePicker() }
        binding.btnEveryday.setOnClickListener { binding.toggleGroupDays.checkAll() }
        binding.btnWeekends.setOnClickListener {
            binding.toggleGroupDays.clearChecked()
            binding.toggleGroupDays.check(R.id.btn_sun)
            binding.toggleGroupDays.check(R.id.btn_sat)
        }
        binding.btnConfirm.setOnClickListener { scheduleAlarm() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    // 전달받은 요일 배열로 토글 버튼 상태 업데이트
    private fun updateDaysUI(days: BooleanArray) {
        val dayButtons = listOf(
            binding.btnSun, binding.btnMon, binding.btnTue, binding.btnWed,
            binding.btnThu, binding.btnFri, binding.btnSat
        )
        days.forEachIndexed { index, isSelected ->
            if (isSelected) {
                binding.toggleGroupDays.check(dayButtons[index].id)
            } else {
                binding.toggleGroupDays.uncheck(dayButtons[index].id)
            }
        }
    }

    private fun showTimePicker() { /* 이전과 동일 */ }
    private fun updateUITime(hour: Int, minute: Int) { /* 이전과 동일 */ }
    private fun scheduleAlarm() { /* 이전과 동일 */ }

    // 모든 버튼을 체크하는 확장 함수
    private fun com.google.android.material.button.MaterialButtonToggleGroup.checkAll() {
        for (i in 0 until childCount) {
            val button = getChildAt(i) as MaterialButton
            this.check(button.id)
        }
    }

    companion object { /* 이전과 동일 */ }
}