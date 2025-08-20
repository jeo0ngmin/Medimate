package com.example.medimate.tts

data class MedInfo(
    val name: String? = null,
    val form: String? = null,
    val strength: String? = null,
    val doseCount: Int? = null,
    val doseUnit: String? = null,
    val timesPerDay: Int? = null,
    val schedules: List<String> = emptyList(),
    val totalDays: Int? = null,
    val storage: String? = null,
    val cautions: List<String> = emptyList()
)

data class ParseResult(
    val medList: List<MedInfo>,
    val globalCautions: List<String> = emptyList()
)

object OcrParser {
    private fun preprocess(raw: String): String {
        var t = raw.replace("\r", " ").replace("\n", " ")
            .replace(Regex("\\s+"), " ").trim()
        t = t.replace("O", "0")
            .replace("l회", "1회").replace("I회", "1회")
            .replace("하루 l회", "하루 1회")
            .replace("식 전", "식전").replace("식 후", "식후")
        return t
    }

    private val nameFormStrength = Regex("""([가-힣A-Za-z0-9/\-+().]+)\s*(\d+\s?mg)?\s*(정제|정|캡슐|환|시럽|액)?""")
    private val dosePerInt = Regex("""1\s*회\s*(?:투약량\s*)?(\d+)\s*(정|캡슐|알|ml)?""")
    private val timesPerDay = Regex("""(?:하루|1일)\s*(\d+)\s*회""")
    private val schedules = Regex("""(아침|점심|저녁|취침전|식전|식후)""")
    private val totalDays = Regex("""(?:총투여일수|총)\s*(\d+)\s*일""")
    private val storage = Regex("""(실온보관|냉장보관)""")
    private val cautionList = listOf("운전", "졸음", "음주", "주의", "기계조작")

    fun parse(raw: String): ParseResult {
        val text = preprocess(raw)
        val meds = mutableListOf<MedInfo>()
        val globalCautions = mutableSetOf<String>()

        // 간단하게 문장 단위 split
        val blocks = text.split(Regex("[.;]")).map { it.trim() }
        for (block in blocks) {
            val m = nameFormStrength.find(block) ?: continue
            val name = m.groupValues.getOrNull(1)
            val strength = m.groupValues.getOrNull(2)
            val form = m.groupValues.getOrNull(3)

            val dose = dosePerInt.find(block)
            val doseCount = dose?.groupValues?.getOrNull(1)?.toIntOrNull()
            val doseUnit = dose?.groupValues?.getOrNull(2)

            val times = timesPerDay.find(block)?.groupValues?.getOrNull(1)?.toIntOrNull()
            val scheds = schedules.findAll(block).map { it.value }.toList()
            val days = totalDays.find(block)?.groupValues?.getOrNull(1)?.toIntOrNull()
            val store = storage.find(block)?.value
            val cauts = cautionList.filter { block.contains(it) }

            meds += MedInfo(name, form, strength, doseCount, doseUnit, times, scheds, days, store, cauts)
            globalCautions += cauts
        }
        return ParseResult(meds, globalCautions.toList())
    }
}
