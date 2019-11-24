package com.space.assistant.service.parser

import JobResultParseType
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobResultParser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class EmptyJobResultParser : JobResultParser {

    override fun parseResult(jobRawResult: JobResult): Mono<JobResult> {
        if (!canParse(jobRawResult)) return Mono.empty()

        val result = jobRawResult.copy()

        return Mono.just(result)
    }

    private fun canParse(jobResult: JobResult): Boolean =
            jobResult.jobInfo.resultParseInfo.type == JobResultParseType.EMPTY
}
