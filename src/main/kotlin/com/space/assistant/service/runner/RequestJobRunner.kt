package com.space.assistant.service.runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobRunnerInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobRunner
import org.springframework.stereotype.Service
import java.net.URL

@Service
class RequestJobRunner(
        private val objectMapper: ObjectMapper
) : JobRunner {

    companion object {
        const val typeName = "REQUEST"
    }

    data class Info(
            val url: String,
            override val type: String = typeName
    ) : JobRunnerInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.runnerInfo as? Info ?: return null

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
