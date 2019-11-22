interface JobResultParseInfo {
    val type: String
}

object JobResultParseType {
    const val PLAIN_TEXT = "PLAIN_TEXT"
    const val JSON_PATH = "JSON_PATH"
}

data class PlainTextJobResultParseInfo(override val type: String = JobResultParseType.PLAIN_TEXT) : JobResultParseInfo

data class JsonPathJobResultParseInfo(
        val jsonPathValues: List<String>,
        val resultFormatString: String
) : JobResultParseInfo {
    override val type: String = JobResultParseType.JSON_PATH
}

