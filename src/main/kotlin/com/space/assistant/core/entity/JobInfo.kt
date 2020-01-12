package com.space.assistant.core.entity

import JobResultParseInfo
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
