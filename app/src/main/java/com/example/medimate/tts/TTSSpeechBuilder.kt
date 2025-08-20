package com.example.medimate.tts

object TTSSpeechBuilder {
    fun toSpeechLines(p: ParseResult): List<String> {
        val lines = mutableListOf<String>()
        if (p.medList.isEmpty()) {
            return listOf("인식된 약 정보가 없습니다.")
        }

        lines += "복약 안내를 시작합니다."
        p.medList.forEachIndexed { idx, m ->
            val nameLine = listOfNotNull(m.name, m.strength, m.form).joinToString(" ")
            lines += "${idx + 1}번 약, ${if (nameLine.isBlank()) "약" else nameLine}."

            if (m.doseCount != null) {
                val unit = m.doseUnit ?: "정"
                lines += "1회에 ${m.doseCount}${unit} 복용하세요."
            }
            if (m.timesPerDay != null) {
                lines += "하루 ${m.timesPerDay}회 복용입니다."
            }
            if (m.schedules.isNotEmpty()) {
                lines += "복용 시간은 ${m.schedules.joinToString(", ")} 입니다."
            }
            if (m.totalDays != null) {
                lines += "총 ${m.totalDays}일 동안 복용하세요."
            }
            if (!m.storage.isNullOrBlank()) {
                lines += "${m.storage} 해 주세요."
            }
            if (m.cautions.isNotEmpty()) {
                lines += "주의 사항: ${m.cautions.joinToString(", ")}. 유의하세요."
            }
        }

        if (p.globalCautions.isNotEmpty()) {
            lines += "추가 주의 키워드 감지: ${p.globalCautions.joinToString(", ")}."
        }
        lines += "복약 안내를 마칩니다."
        return lines
    }
}
