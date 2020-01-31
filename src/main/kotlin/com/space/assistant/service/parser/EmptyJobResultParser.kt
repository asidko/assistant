package com.space.assistant.service.parser

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.emptyJobResult
import com.space.assistant.core.service.JobResultParser
import org.springframework.stereotype.Service

@Service
class EmptyJobResultParser : JobResultParser {

    override suspend fun parseResult(activeJobInfo: ActiveJobInfo): JobResult? {
        if (activeJobInfo.jobInfo?.resultParseInfo !is EmptyJobResultParser) return null
        return activeJobInfo.jobRawResult ?: emptyJobResult
    }
}
