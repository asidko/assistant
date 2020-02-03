package com.space.assistant.core.entity

import com.space.assistant.core.util.simpleID

data class JobInfo(
        val uuid: String = simpleID(),
        val finderInfo: JobFinderInfo,
        val phraseBefore: List<String>,
        val runnerInfo: JobRunnerInfo,
        val resultParserInfo: JobResultParserInfo,
        val phraseAfter: List<String>,
        val redirectToJobs: List<String>
)

interface JobFinderInfo {
    val type: String
}

interface JobResultParserInfo {
    val type: String
}

interface JobRunnerInfo {
    val type: String
}
