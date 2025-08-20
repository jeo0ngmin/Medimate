package com.example.medimate

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medimate.databinding.ItemAlarmBinding

// 데이터 모양 정의
data class Alarm(
    val time: String,
    val daysSelected: BooleanArray,
    var isEnabled: Boolean
)

class AlarmAdapter(private val alarmList: List<Alarm>) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedAlarm = alarmList[position]
                    val intent = Intent(itemView.context, AlarmSettingActivity::class.java).apply {
                        // ▼▼ "EXISTING_ALARM_DAYS_BOOLEAN" 라는 이름으로 BooleanArray를 전달합니다. ▼▼
                        putExtra("EXISTING_ALARM_TIME", clickedAlarm.time)
                        putExtra("EXISTING_ALARM_DAYS_BOOLEAN", clickedAlarm.daysSelected)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }

        fun bind(alarm: Alarm) {
            binding.tvAlarmTime.text = alarm.time
            binding.switchAlarmItem.isChecked = alarm.isEnabled

            val dayTextViews = listOf(
                binding.tvDaySun, binding.tvDayMon, binding.tvDayTue, binding.tvDayWed,
                binding.tvDayThu, binding.tvDayFri, binding.tvDaySat
            )

            alarm.daysSelected.forEachIndexed { index, isSelected ->
                val textView = dayTextViews[index]
                if (isSelected) {
                    textView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
                    textView.setTypeface(null, Typeface.BOLD)
                } else {
                    textView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                    textView.setTypeface(null, Typeface.NORMAL)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        // ▼▼ item_alarm.xml과 연결되도록 ItemAlarmBinding을 사용해야 합니다. ▼▼
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarmList[position])
    }

    override fun getItemCount(): Int = alarmList.size
}