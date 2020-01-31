package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.TimeDelayJobExecInfo
import com.space.assistant.core.service.JobRunner
import kotlinx.coroutines.delay
import org.springframework.stereotype.Service

@Service
class TimeDelayJobRunner : JobRunner {

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? TimeDelayJobExecInfo ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val millis = execInfo.seconds.toLong() * 1000

        delay(millis) // <---- wait for given time

        return prevJobResult?.copy() ?: JobResult(execInfo.seconds)
    }
}
