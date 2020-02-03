package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobRunnerInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobRunner
import kotlinx.coroutines.delay
import org.springframework.stereotype.Service

@Service
class TimeDelayJobRunner : JobRunner {

    companion object {
        const val typeName = "TIME_DELAY"
    }

    data class Info(
            val seconds: String,
            override val type: String = typeName
    ) : JobRunnerInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.runnerInfo as? Info ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val millis = execInfo.seconds.toLong() * 1000

        delay(millis) // <---- wait for given time

        return prevJobResult?.copy() ?: JobResult(execInfo.seconds)
    }
}
