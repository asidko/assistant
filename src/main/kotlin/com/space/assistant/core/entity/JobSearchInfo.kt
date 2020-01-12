package com.space.assistant.core.entity

interface JobSearchInfo {
    val type: String
}

object JobSearchType {
    const val EMPTY = "EMPTY"
    const val DIRECT_MATCH = "DIRECT_MATCH"
    const val WILDCARD = "WILDCARD"
}

data class EmptyJobSearchInfo(override val type: String = JobSearchType.EMPTY) : JobSearchInfo

data class DirectMatchJobSearchInfo(val texts: List<String>) : JobSearchInfo {
    override val type: String = JobSearchType.DIRECT_MATCH
}

data class WildcardJobSearchInfo(val text: String) : JobSearchInfo {
    override val type: String = JobSearchType.WILDCARD
}
