package com.space.assistant.service.parser

import JobResultParseType
import JsonPathJobResultParseInfo
import com.jayway.jsonpath.JsonPath
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.emptyJobResult
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.PatternStringReplacer
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JsonPathJobResultParser(
        val patternStringReplacer: PatternStringReplacer
) : JobResultParser {

    override fun parseResult(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canParse(activeJobInfo)) return Mono.empty()

        return Mono.create {
            val jobRawResult = activeJobInfo.jobRawResult

            val json = jobRawResult?.value ?: return@create it.success(emptyJobResult)

            val jsonPathList = getJsonPathValues(activeJobInfo)
            val resultFormatString = getResultFormatString(activeJobInfo)

            val jsonPathValues = jsonPathList.map { path -> JsonPath.read<Any>(json, path) }

            val resultString = patternStringReplacer.replacePattern(resultFormatString, jsonPathValues)

            it.success(jobRawResult.copy(value = resultString))
        }
    }

    private fun getJsonPathValues(activeJobInfo: ActiveJobInfo) =
            (activeJobInfo.jobInfo?.resultParseInfo as? JsonPathJobResultParseInfo)?.jsonPathValues ?: emptyList()

    private fun getResultFormatString(activeJobInfo: ActiveJobInfo) =
            (activeJobInfo.jobInfo?.resultParseInfo as? JsonPathJobResultParseInfo)?.resultFormatString ?: ""

    private fun canParse(activeJobInfo: ActiveJobInfo): Boolean =
            activeJobInfo.jobInfo?.resultParseInfo?.type == JobResultParseType.JSON_PATH
}
