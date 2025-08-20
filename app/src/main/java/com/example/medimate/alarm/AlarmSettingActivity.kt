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
import com.example.medimate.alarm.AlarmReceiver
import com.example.medimate.databinding.ActivityAlarmSettingBinding
import java.util.Calendar
import java.util.Locale

class AlarmSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmSettingBinding
    private var alarmHour: Int = 0
    private var alarmMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 현재 시간으로 변수 초기화 및 화면 업데이트
        val calendar = Calendar.getInstance()
        alarmHour = calendar.get(Calendar.HOUR_OF_DAY)
        alarmMinute = calendar.get(Calendar.MINUTE)
        updateUITime(alarmHour, alarmMinute)

        // 시간 텍스트를 누르면 TimePickerDialog 실행
        binding.tvTimeValue.setOnClickListener {
            showTimePicker()
        }

        // 확인 버튼
        binding.btnConfirm.setOnClickListener {
            scheduleAlarm()
        }
        // 취소 버튼 (닫기)
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    // 동그란 시간 선택창을 보여주는 함수
    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // 사용자가 선택한 시간으로 변수 업데이트 및 화면 새로고침
                alarmHour = hourOfDay
                alarmMinute = minute
                updateUITime(hourOfDay, minute)
            },
            alarmHour,
            alarmMinute,
            false // 24시간 형식이 아닌 AM/PM 형식 사용
        )
        timePickerDialog.show()
    }

    // 선택된 시간을 화면의 텍스트에 예쁘게 표시하는 함수
    private fun updateUITime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "오전" else "오후"
        val displayHour = if (calendar.get(Calendar.HOUR) == 0) 12 else calendar.get(Calendar.HOUR)
        binding.tvTimeValue.text = String.format(Locale.getDefault(), "%s %d:%02d", amPm, displayHour, minute)
    }

    // 알람을 시스템에 등록하는 함수
    private fun scheduleAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        // ▼▼ 요일 선택이 없어졌으므로, '매일'을 의미하는 Boolean 배열을 직접 생성 ▼▼
        val everyDay = BooleanArray(7) { true }
        intent.putExtra("SELECTED_DAYS", everyDay)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(), // requestCode를 고유하게 설정
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmHour)
            set(Calendar.MINUTE, alarmMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }
}