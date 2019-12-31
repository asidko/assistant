package com.space.assistant.service.parser

import JobResultParseType
import JsonPathJobResultParseInfo
import com.jayway.jsonpath.JsonPath
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.core.service.StringProcessor
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JsonPathJobResultParser(
        val stringProcessors: List<StringProcessor>
) : JobResultParser {

    override fun parseResult(jobRawResult: JobResult): Mono<JobResult> {
        if (!canParse(jobRawResult)) return Mono.empty()

        return Mono.create {
            val json = jobRawResult.result
            val jsonPathList = getJsonPathValues(jobRawResult)
            val resultFormatString = getResultFormatString(jobRawResult)

            val jsonPathValues = jsonPathList.map { path -> JsonPath.read<Any>(json, path) }

            var resultString = resultFormatString
            for (i in 0..jsonPathValues.lastIndex)
                resultString = resultString.replace("$${i + 1}", jsonPathValues[i].toString())

            resultString = stringProcessors.fold(resultString, { text, processor -> processor.process(text) })


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
