package com.space.assistant.service.parser

import JobResultParseType
import PatternStringResultParseInfo
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.asArgs
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.PatternStringReplacer
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PatternStringJobResultParser(
        val patternStringReplacer: PatternStringReplacer
) : JobResultParser {

    override fun parseResult(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canParse(activeJobInfo)) return Mono.empty()

        return Mono.create {
            val args = activeJobInfo.jobRawResult?.asArgs()
            val pattern = (activeJobInfo.jobInfo?.resultParseInfo as? PatternStringResultParseInfo)?.text ?: ""

            val resultString = patternStringReplacer.replacePattern(pattern, args)

            val result = JobResult(resultString)
            it.success(result)
        }
    }


    private fun canParse(activeJobInfo: ActiveJobInfo): Boolean =
            activeJobInfo.jobInfo?.resultParseInfo?.type == JobResultParseType.PATTERN_STRING
}
