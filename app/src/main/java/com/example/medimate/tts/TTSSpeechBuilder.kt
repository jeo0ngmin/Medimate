package com.example.medimate.tts

object TTSSpeechBuilder {

    fun toSpeechLines(p: OcrParser.ParsedResult): List<String> {
        val lines = mutableListOf<String>()

        if (p.drugNames.isEmpty() && p.timesPerDay == null && p.mealHints.isEmpty()) {
            return listOf("인식된 복약 정보가 거의 없습니다. 다시 시도해주세요.")
        }

        lines += "복약 안내를 시작합니다."

        // 1. 약 이름 안내
        if (p.drugNames.isNotEmpty()) {
            val names = p.drugNames.joinToString(", ")
            lines += "처방된 약은 ${names} 등 입니다."
        }

        // 2. 복용 횟수 및 시간 안내
        val timeSentence = mutableListOf<String>()
        p.timesPerDay?.let { timeSentence.add("하루 ${it}회") }
        if (p.mealHints.isNotEmpty()) {
            timeSentence.add(p.mealHints.joinToString(", "))
        }
        if (timeSentence.isNotEmpty()) {
            lines += timeSentence.joinToString(" ") + " 복용하세요."
        }

        // 3. 1회 용량 안내
        p.doseText?.let {
            lines += "한 번에 ${it}씩 드세요."
        }

        // 4. 총 복용 일수 안내
        p.totalDays?.let {
            lines += "총 ${it}일 동안 복용하세요."
        }

        // 5. 주의사항 안내
        if (p.cautions.isNotEmpty()) {
            lines += "주의사항입니다."
            lines.addAll(p.cautions)
        }

        lines += "복약 안내를 마칩니다."
        return lines
    }
}