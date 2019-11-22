package com.space.assistant.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URL

@Service
class RequestJobRunner(
        private val objectMapper: ObjectMapper
) : JobRunner {

    override fun runJob(jobInfo: JobInfo): Mono<JobResult> {
        if (!canRun(jobInfo)) return Mono.empty();

        return Mono.create {
            val url = jobInfo.execValue
            val json = sendRequest(url)
            val result = JobResult(json, jobInfo)

            it.success(result)
        }
    }

    private fun canRun(jobInfo: JobInfo) =
            jobInfo.execType == JobExecType.GET_REQUEST

    private fun sendRequest(url: String): String {
        return URL(url)
                .openConnection()
                .getInputStream().use {
                    objectMapper.readTree(it).toString()
                }
    }
}
