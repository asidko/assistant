package com.space.assistant.service.parser

import PatternStringJobResultParseInfo
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.asArgs
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.PatternStringReplacer
import org.springframework.stereotype.Service

@Service
class PatternStringJobResultParser(
        val patternStringReplacer: PatternStringReplacer
) : JobResultParser {

    override suspend fun parseResult(activeJobInfo: ActiveJobInfo): JobResult? {
        val resultParseInfo = activeJobInfo.jobInfo?.resultParseInfo as? PatternStringJobResultParseInfo ?: return null

        val pattern = resultParseInfo.text
        val patternArgs = activeJobInfo.jobRawResult?.asArgs()

        val resultString = patternStringReplacer.replacePattern(pattern, patternArgs)

        return JobResult(resultString)
    }
}
