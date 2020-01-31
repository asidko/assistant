interface JobResultParseInfo {
    val type: String
}

object JobResultParseType {
    const val EMPTY = "PLAIN_TEXT"
    const val JSON_PATH = "JSON_PATH"
    const val PATTERN_STRING = "PATTERN_STRING"
}

data class EmptyJobResultParseInfo(override val type: String = JobResultParseType.EMPTY) : JobResultParseInfo

data class JsonPathJobResultParseInfo(
        val jsonPathValues: List<String>,
        val resultFormatString: String
) : JobResultParseInfo {
    override val type: String = JobResultParseType.JSON_PATH
}

data class PatternStringJobResultParseInfo(val text: String) : JobResultParseInfo {
    override val type: String = JobResultParseType.PATTERN_STRING
}

