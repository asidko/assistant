package com.space.assistant.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.RequestJobExecInfo
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import java.net.URL

@Service
class RequestJobRunner(
        private val objectMapper: ObjectMapper
) : JobRunner {

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? RequestJobExecInfo ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val url = prevJobResult?.value
                ?: execInfo.url
                ?: return null

        val json = sendRequest(url)

        return JobResult(json)
    }


    private fun sendRequest(url: String): String = URL(url)
            .openConnection()
            .getInputStream()
            .use {
                objectMapper.readTree(it).toString()
            }
}
