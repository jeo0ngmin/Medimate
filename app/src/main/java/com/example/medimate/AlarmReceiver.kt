package com.example.medimate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // ▼▼ 설정창에서 보낸 요일 정보를 받습니다. ▼▼
        val selectedDays = intent.getBooleanArrayExtra("SELECTED_DAYS") ?: return

        val today = Calendar.getInstance()
        val todayIndex = today.get(Calendar.DAY_OF_WEEK) - 1 // 일요일=0, 월요일=1...

        // ▼▼ 오늘이 선택된 요일이 아니라면 알림을 띄우지 않고 종료합니다. ▼▼
        if (!selectedDays[todayIndex]) {
            return
        }

        // (이하 알림 띄우는 코드는 동일)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "약 복용 알림", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle("복용 알림")
            .setContentText("약 드실 시간입니다!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }
}