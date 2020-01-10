package com.space.assistant.core.entity

import JobResultParseInfo
import java.util.*

data class JobInfo(
        val uuid: String = UUID.randomUUID().toString(),
        val searchInfo: JobSearchInfo,
        val preExecPhrase: List<String>,
        val execInfo: JobExecInfo,
        val resultParseInfo: JobResultParseInfo,
        val postExecPhrase: List<String>,
        val redirectToJobs: List<String>
)
