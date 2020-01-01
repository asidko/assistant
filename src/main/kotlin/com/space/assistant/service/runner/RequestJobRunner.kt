package com.space.assistant.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URL

@Service
class RequestJobRunner(
        private val objectMapper: ObjectMapper
) : JobRunner {

    override fun runJob(runJobInfo: RunJobInfo): Mono<JobResult> {
        if (!canRun(runJobInfo.jobInfo)) return Mono.empty()

        return Mono.create {
            val url = runJobInfo.previousJobResult?.result ?: (runJobInfo.jobInfo.execInfo as RequestJobExecInfo).url
            val json = sendRequest(url)
            val result = runJobInfo.previousJobResult?.copy(result = json)
                    ?: JobResult.new(json, runJobInfo.jobInfo)

            it.success(result)
        }
    }

    private fun canRun(jobInfo: JobInfo) =
            jobInfo.execInfo.type == JobExecType.REQUEST

    private fun sendRequest(url: String): String {
        return URL(url)
                .openConnection()
                .getInputStream().use {
                    objectMapper.readTree(it).toString()
                }
    }
}
