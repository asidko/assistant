package com.space.assistant.service.parser

import JobResultParseType
import PatternStringResultParseInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.PatternStringReplacer
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PatternStringJobResultParser(
        val patternStringReplacer: PatternStringReplacer
) : JobResultParser {

    override fun parseResult(jobRawResult: JobResult): Mono<JobResult> {
        if (!canParse(jobRawResult)) return Mono.empty()

        return Mono.create {
            val args = jobRawResult.result.split(",").map(String::trim)
            val pattern = (jobRawResult.jobInfo.resultParseInfo as PatternStringResultParseInfo).text

            val resultString = patternStringReplacer.replacePattern(pattern, args)

            it.success(jobRawResult.copy(result = resultString))
        }
    }


    private fun canParse(jobResult: JobResult): Boolean =
            jobResult.jobInfo.resultParseInfo.type == JobResultParseType.PATTERN_STRING
}
