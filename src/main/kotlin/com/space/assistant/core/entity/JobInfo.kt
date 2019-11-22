package com.space.assistant.core.entity

import java.util.*

class JobInfo(
        val uuid: UUID = UUID.randomUUID(),
        val parseInfo: JobParseInfo,
        val execType: String,
        val execValue: String,
        val resultParseType: String,
        val resultParseValue: String,
        val shouldSayResult: Boolean = false
)

object JobParseType {
    val DIRECT_MATCH = "DIRECT_MATCH"
}

object JobExecType {
    val NONE = "NONE"
    val GET_REQUEST = "GET_REQUEST"
    val JUST_SAY = "JUST_SAY"
}

object JobResultParseType {
    var PLAIN_TEXT = "PLAIN_TEXT"
    var JSON_PATH = "JSON_PATH"
}
