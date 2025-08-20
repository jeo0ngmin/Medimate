package com.example.medimate.tts

/**
 * OCRParser.ParsedResult -> TTS 낭독용 문장 리스트로 변환
 * - 핵심만 간결하게, 약 이름은 전부 나열
 */
object TTSSpeechBuilder {

    fun toSpeechLines(r: OCRParser.ParsedResult): List<String> {
        val lines = mutableListOf<String>()

        // 1) 약 이름: "등 총 N개" 없이 전부 나열
        val names = r.drugNames.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (names.isNotEmpty()) {
            lines += "인식된 약품은 " + names.joinToString(", ") + " 입니다."
        } else {
            lines += "약품명이 명확하지 않습니다. 복용 안내만 먼저 읽어 드릴게요."
        }

        // 2) 공통 복용 스케줄
        val schedule = buildScheduleSentence(r)
        val dose     = r.doseText?.let { " 1회 ${it} 복용" } ?: ""
        val total    = r.totalDays?.let { " 총 ${it}일간 복용" } ?: ""

        if (schedule != null || dose.isNotEmpty() || total.isNotEmpty()) {
            val body = listOfNotNull(schedule).joinToString()
            val tail = (dose + total).trim()
            val merged = (if (body.isNotEmpty()) body else "") + (if (tail.isNotEmpty()) ", $tail" else "")
            if (merged.isNotEmpty()) lines += merged + " 하세요."
        }

        // 3) 주의사항
        if (r.cautions.isNotEmpty()) {
            val cautionLine = r.cautions.joinToString(separator = ", ")
            lines += "주의사항을 안내합니다. $cautionLine."
        }

        // 4) 마무리
        lines += "복용 중 이상 증상이 있으면 전문가와 상의하세요."

        return lines
    }

    private fun buildScheduleSentence(r: OCRParser.ParsedResult): String? {
        val t = r.timesPerDay
        val meals = r.mealHints

        val timePart = when (t) {
            null -> null
            else -> "하루 ${t}회"
        }

        // mealHints가 ["아침","점심","저녁","식후 30분"] 같이 섞여 들어올 수 있으므로 그대로 나열
        val mealJoined = joinKoreanList(meals)
        val mealPart = if (mealJoined.isBlank()) null else {
            // "식후/식전/분"이 포함되면 따로 "에"를 붙이지 않음
            if (meals.any { it.contains("식") || it.contains("분") }) mealJoined
            else "$mealJoined 에"
        }

        return when {
            timePart != null && mealPart != null -> "$timePart, $mealPart 복용"
            timePart != null -> "$timePart 복용"
            mealPart != null -> "$mealPart 복용"
            else -> null
        }
    }

    /** 한국어 나열: [아침, 점심, 저녁] -> "아침, 점심, 저녁" / [아침, 저녁] -> "아침, 저녁" */
    private fun joinKoreanList(list: List<String>): String {
        val d = list.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (d.isEmpty()) return ""
        if (d.size == 1) return d.first()
        val head = d.dropLast(1).joinToString(separator = ", ")
        val last = d.last()
        return "$head, $last"
    }
}
