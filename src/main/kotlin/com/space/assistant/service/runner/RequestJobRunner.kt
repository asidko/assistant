package com.space.assistant.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.RequestJobExecInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URL

@Service
class RequestJobRunner(
        private val objectMapper: ObjectMapper
) : JobRunner {

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canRun(activeJobInfo)) return Mono.empty()

        val url = activeJobInfo.prevActiveJobInfo?.jobResult?.value
                ?: (activeJobInfo.jobInfo?.execInfo as? RequestJobExecInfo)?.url
                ?: return Mono.empty()

        return Mono.create {
            val json = sendRequest(url)
            val result = JobResult(json)
            it.success(result)
        }
    }

    private fun canRun(activeJobInfo: ActiveJobInfo) =
            activeJobInfo.jobInfo?.execInfo?.type == JobExecType.REQUEST

    private fun sendRequest(url: String): String {
        return URL(url)
                .openConnection()
                .getInputStream().use {
                    objectMapper.readTree(it).toString()
                }
    }
}
