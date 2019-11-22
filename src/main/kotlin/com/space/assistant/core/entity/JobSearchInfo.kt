package com.space.assistant.core.entity

interface JobSearchInfo {
    val type: String
}

object JobParseType {
    const val NONE = "NONE"
    const val DIRECT_MATCH = "DIRECT_MATCH"
}

data class EmptyJobSearchInfo(override val type: String = JobParseType.NONE) : JobSearchInfo

data class DirectMatchJobSearchInfo(val text: String) : JobSearchInfo {
    override val type: String = JobParseType.DIRECT_MATCH
}
