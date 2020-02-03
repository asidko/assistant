package com.space.assistant.core.entity

import com.space.assistant.core.util.simpleID

data class JobInfo(
        val uuid: String = simpleID(),
        val searchInfo: JobSearchInfo,
        val preExecPhrase: List<String>,
        val execInfo: JobExecInfo,
        val resultParseInfo: JobResultParseInfo,
        val postExecPhrase: List<String>,
        val redirectToJobs: List<String>
)

interface JobSearchInfo {
    val type: String
}

interface JobResultParseInfo {
    val type: String
}

interface JobExecInfo {
    val type: String
}
