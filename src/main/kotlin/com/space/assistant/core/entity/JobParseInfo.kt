package com.space.assistant.core.entity

interface JobParseInfo {
    val parseType: String
}

class DirectMatchJobParseInfo(val text: String) : JobParseInfo {
    override val parseType: String
        get() = JobParseType.DIRECT_MATCH
}
