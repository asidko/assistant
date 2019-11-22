package com.space.assistant.service

import com.jayway.jsonpath.JsonPath
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.JobResultParseType
import com.space.assistant.core.service.JobResultParser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JsonPathJobResultParser : JobResultParser {

    override fun parseResult(jobRawResult: JobResult): Mono<JobResult> {
        if (!canParse(jobRawResult)) return Mono.empty()

        return Mono.create {
            val json = jobRawResult.result
            val parseResult = JsonPath.parse(json)
            val result = parseResult.toString()

            it.success(JobResult(result, jobRawResult.jobInfo))
        }
    }

    private fun canParse(jobResult: JobResult): Boolean =
            jobResult.jobInfo.resultParseType == JobResultParseType.JSON_PATH
}
