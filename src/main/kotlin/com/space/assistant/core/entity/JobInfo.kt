package com.space.assistant.core.entity

import JobExecInfo
import JobResultParseInfo
import java.util.*

class JobInfo(
        val uuid: UUID = UUID.randomUUID(),
        val searchInfo: JobSearchInfo,
        val execInfo: JobExecInfo,
        val resultParseInfo: JobResultParseInfo
)
