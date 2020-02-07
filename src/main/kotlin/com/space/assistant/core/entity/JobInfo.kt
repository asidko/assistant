package com.space.assistant.core.entity

import com.space.assistant.core.util.simpleID

data class JobInfo(
        val uuid: String = simpleID(),
        val activatorInfo: JobActivatorInfo,
        val phrase: JobPhrase,
        val runnerInfo: JobRunnerInfo,
        val resultParserInfo: JobResultParserInfo,
        val redirectToJobs: List<String>
)

data class JobPhrase(
        val before: List<String>,
        val after: List<String>
)

interface JobActivatorInfo {
    val type: String
}

interface JobResultParserInfo {
    val type: String
}

interface JobRunnerInfo {
    val type: String
}
