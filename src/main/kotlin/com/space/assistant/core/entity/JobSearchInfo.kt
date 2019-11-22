package com.space.assistant.core.entity

interface JobSearchInfo {
    val type: String
}

object JobParseType {
    const val DIRECT_MATCH = "DIRECT_MATCH"
}

class DirectMatchJobSearchInfo(val text: String) : JobSearchInfo {
    override val type: String = JobParseType.DIRECT_MATCH
}
