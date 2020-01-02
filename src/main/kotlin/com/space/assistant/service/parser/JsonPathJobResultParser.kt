package com.space.assistant.service.parser

import JobResultParseType
import JsonPathJobResultParseInfo
import com.jayway.jsonpath.JsonPath
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.service.PatternStringReplacer
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JsonPathJobResultParser(
        val patternStringReplacer: PatternStringReplacer
) : JobResultParser {

    override fun parseResult(jobRawResult: JobResult): Mono<JobResult> {
        if (!canParse(jobRawResult)) return Mono.empty()

        return Mono.create {
            val json = jobRawResult.result
            val jsonPathList = getJsonPathValues(jobRawResult)
            val resultFormatString = getResultFormatString(jobRawResult)

            val jsonPathValues = jsonPathList.map { path -> JsonPath.read<Any>(json, path) }

            val resultString = patternStringReplacer.replacePattern(resultFormatString, jsonPathValues)

            it.success(jobRawResult.copy(result = resultString))
        }
    }

    private fun getJsonPathValues(jobRawResult: JobResult) =
            (jobRawResult.jobInfo.resultParseInfo as JsonPathJobResultParseInfo).jsonPathValues

    private fun getResultFormatString(jobRawResult: JobResult) =
            (jobRawResult.jobInfo.resultParseInfo as JsonPathJobResultParseInfo).resultFormatString

    private fun canParse(jobResult: JobResult): Boolean =
            jobResult.jobInfo.resultParseInfo.type == JobResultParseType.JSON_PATH
}
