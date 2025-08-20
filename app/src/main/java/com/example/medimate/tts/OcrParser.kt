package com.example.medimate.tts

// 다른 곳에서 쉽게 사용하도록 object로 변경
object OcrParser {

    // ---------- 공개 데이터 모델 ----------
    data class ParsedResult(
        val drugNames: List<String>,   // 약품명 후보들
        val timesPerDay: Int?,         // 1일 복용 횟수 (ex. 3)
        val mealHints: List<String>,   // 아침/점심/저녁/취침 전/식전/식후/직후/30분 등
        val doseText: String?,         // 1회 용량 텍스트 (ex. "1정", "2캡슐")
        val totalDays: Int?,           // 총 복용 일수 (ex. 3)
        val cautions: List<String>     // 주의사항 키워드 문구
    )

    // ---------- 외부 진입 ----------
    fun parse(raw: String): ParsedResult {
        val text = normalize(raw)

        val drugs      = extractDrugNames(text)
        val times      = extractTimesPerDay(text)
        val meals      = extractMealHints(text)
        val dose       = extractDose(text)
        val days       = extractTotalDays(text)
        val cautions   = extractCautions(text)

        return ParsedResult(
            drugNames  = drugs,
            timesPerDay = times,
            mealHints   = meals,
            doseText    = dose,
            totalDays   = days,
            cautions    = cautions
        )
    }

    // ---------- 전처리 ----------
    private fun normalize(src: String): String {
        return src
            .replace("\r", " ")
            .replace("\n", " ")
            .replace("""\s+""".toRegex(), " ")
            .replace("１","1").replace("２","2").replace("３","3").replace("４","4")
            .replace("５","5").replace("６","6").replace("７","7").replace("８","8").replace("９","9").replace("０","0")
            .trim()
    }

    // ---------- 약품명 ----------
    private fun extractDrugNames(text: String): List<String> {
        val tail = "(정|정제|캡슐|서방정|장용정|현탁액|시럽|액|과립|산|연질캡슐)"
        val unit = "(?:mg|㎎|g|mcg|㎍|µg|ml|mL|㎖)"
        val nameRegex = Regex("""([가-힣A-Za-z0-9\-\+\(\)·]+)$tail?\s*\d+(?:\.\d+)?\s*$unit""")

        val found = nameRegex.findAll(text)
            .map { it.groupValues[1] }
            .map { it.trim() }
            .filter { it.length in 2..40 }
            .distinct()
            .toList()

        return (found)
            .distinct()
            .take(15)
    }

    // ---------- 1일 복용 횟수 ----------
    private fun extractTimesPerDay(text: String): Int? {
        val n1 = Regex("""(?:1일|하루|1日)\s*([0-9]+)\s*(?:회|번)""").find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
        if (n1 != null) return n1
        val map = mapOf("한" to 1, "두" to 2, "세" to 3, "네" to 4, "다섯" to 5)
        val n2 = Regex("""(?:1일|하루)\s*([가-힣]+)\s*(?:회|번)""").find(text)?.groupValues?.getOrNull(1)?.let { map[it] }
        if (n2 != null) return n2
        val n3 = Regex("""(?:횟수|회수)\s*([0-9]+)""").find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
        return n3
    }

    // ---------- 복용 시간/식사 힌트 ----------
    private fun extractMealHints(text: String): List<String> {
        val hits = mutableListOf<String>()
        if (text.contains("아침")) hits += "아침"
        if (text.contains("점심")) hits += "점심"
        if (text.contains("저녁")) hits += "저녁"
        if (text.contains("취침 전") || text.contains("취침전") || text.contains("취침")) hits += "취침 전"
        val meal = mutableListOf<String>()
        if (Regex("""식전|식사\s*전""").containsMatchIn(text)) meal += "식전"
        if (Regex("""식후|식사\s*후|직후""").containsMatchIn(text)) meal += "식후"
        if (Regex("""30\s*분""").containsMatchIn(text)) meal += "30분"
        val timePart = hits.distinct()
        val mealPart = meal.distinct()
        return (timePart + mealPart).distinct()
    }

    // ---------- 1회 용량 ----------
    private fun extractDose(text: String): String? {
        val m1 = Regex("""([0-9]+)\s*(정|캡슐|포|회|mL|ml|㎖)""").find(text)?.let {
            it.groupValues[1] + it.groupValues[2]
        }
        if (m1 != null) return m1
        val m2 = Regex("""투약량\s*([0-9]+)\s*(정|캡슐|포|mL|ml|㎖)""").find(text)?.let {
            it.groupValues[1] + it.groupValues[2]
        }
        return m2
    }

    // ---------- 총 복용 일수 ----------
    private fun extractTotalDays(text: String): Int? {
        val r1 = Regex("""총\s*투여\s*일수\s*([0-9]+)\s*일""").find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
        if (r1 != null) return r1
        val r2 = Regex("""([0-9]+)\s*일\s*분""").find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
        if (r2 != null) return r2
        val r3 = Regex("""총\s*([0-9]+)\s*일""").find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()
        return r3
    }

    // ---------- 주의사항 ----------
    private val cautionDict = listOf(
        "어지러움", "졸음", "운전", "기계조작", "위장장애", "속쓰림", "구역", "구토",
        "혈압", "저혈당", "출혈", "알레르기", "발진", "가려움", "간장", "신장", "음주",
        "임신", "수유", "변비", "설사", "빈뇨", "야간뇨", "부종", "체중증가"
    )
    private fun extractCautions(text: String): List<String> {
        val hits = cautionDict.filter { text.contains(it) }
        val sentences = hits.map { keyword ->
            when (keyword) {
                "운전" -> "운전이나 기계 조작 시 주의하세요"
                "졸음" -> "졸음이 올 수 있어 주의하세요"
                "어지러움" -> "어지러움이 나타나면 앉아서 쉬세요"
                "위장장애" -> "위장장애가 있으면 전문의와 상의하세요"
                else -> "$keyword 관련 증상에 주의하세요"
            }
        }
        return sentences.distinct()
    }
}