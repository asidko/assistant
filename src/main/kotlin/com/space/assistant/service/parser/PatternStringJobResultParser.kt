package com.space.assistant.service.parser

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.JobResultParserInfo
import com.space.assistant.core.entity.asArgs
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.text.VariablesInTextReplacer
import org.springframework.stereotype.Service

@Service
class PatternStringJobResultParser(
        val variablesInTextReplacer: VariablesInTextReplacer
) : JobResultParser {
    companion object {
        const val typeName = "PATTERN_STRING"
    }

    data class Info(
            val text: String,
            override val type: String = typeName
    ) : JobResultParserInfo

    override suspend fun parseResult(activeJobInfo: ActiveJobInfo): JobResult? {
        val resultParseInfo = activeJobInfo.jobInfo?.resultParserInfo as? Info ?: return null

        val pattern = resultParseInfo.text
        val patternArgs = activeJobInfo.jobRawResult?.asArgs()

        val resultString = variablesInTextReplacer.replacePattern(pattern, patternArgs)

        return JobResult(resultString)
    }
}
