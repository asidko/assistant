package com.space.assistant.service.parser

import JobResultParseType
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.emptyJobResult
import com.space.assistant.core.service.JobResultParser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class EmptyJobResultParser : JobResultParser {

    override fun parseResult(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canParse(activeJobInfo)) return Mono.empty()

        val result = activeJobInfo.jobRawResult ?: emptyJobResult

        return Mono.just(result)
    }

    private fun canParse(activeJobInfo: ActiveJobInfo): Boolean =
            activeJobInfo.jobInfo?.resultParseInfo?.type == JobResultParseType.EMPTY
}
