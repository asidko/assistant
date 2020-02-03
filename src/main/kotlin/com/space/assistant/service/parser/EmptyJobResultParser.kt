package com.space.assistant.service.parser

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.JobResultParserInfo
import com.space.assistant.core.entity.emptyJobResult
import com.space.assistant.core.service.JobResultParser
import org.springframework.stereotype.Service

@Service
class EmptyJobResultParser : JobResultParser {
    companion object {
        const val typeName = "EMPTY"
    }

    data class Info(
            override val type: String = typeName
    ) : JobResultParserInfo

    override suspend fun parseResult(activeJobInfo: ActiveJobInfo): JobResult? {
        if (activeJobInfo.jobInfo?.resultParserInfo !is Info) return null
        return activeJobInfo.jobRawResult ?: emptyJobResult
    }
}
