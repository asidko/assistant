package com.space.assistant.service.parser

import JsonPathJobResultParseInfo
import com.jayway.jsonpath.JsonPath
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.emptyJobResult
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.PatternStringReplacer
import org.springframework.stereotype.Service

@Service
class JsonPathJobResultParser(
        val patternStringReplacer: PatternStringReplacer
) : JobResultParser {

    override suspend fun parseResult(activeJobInfo: ActiveJobInfo): JobResult? {
        val resultParseInfo = activeJobInfo.jobInfo?.resultParseInfo as? JsonPathJobResultParseInfo ?: return null

        val jobRawResult = activeJobInfo.jobRawResult ?: return emptyJobResult

        val json = jobRawResult.value
        val jsonPathList = resultParseInfo.jsonPathValues
        val resultFormatString = resultParseInfo.resultFormatString

        val jsonPathValues = jsonPathList.map { path -> JsonPath.read<Any>(json, path) }

        val resultString = patternStringReplacer.replacePattern(resultFormatString, jsonPathValues)

        return JobResult(resultString)
    }
}
