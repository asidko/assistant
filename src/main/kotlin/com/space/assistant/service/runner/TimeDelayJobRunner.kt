package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.TimeDelayJobExecInfo
import com.space.assistant.core.service.JobRunner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TimeDelayJobRunner : JobRunner {

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canRun(activeJobInfo)) return Mono.empty()


        val seconds = (activeJobInfo.jobInfo?.execInfo as? TimeDelayJobExecInfo)?.seconds
                ?: return Mono.empty()


        return Mono.create {
            GlobalScope.launch {
                delay(seconds.toLong() * 1000) // <---- wait for given time
                val result = JobResult(seconds)
                it.success(result)
            }
        }
    }


    private fun canRun(activeJobInfo: ActiveJobInfo) = activeJobInfo.jobInfo?.execInfo?.type == JobExecType.TIME_DELAY
}
