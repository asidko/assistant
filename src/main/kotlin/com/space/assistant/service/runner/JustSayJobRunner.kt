package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.JustSayJobExecInfo
import com.space.assistant.core.service.JobRunner
import com.space.assistant.core.service.SpeakService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JustSayJobRunner(
        private val speakService: SpeakService
) : JobRunner {

    override fun runJob(activeJobInfo: ActiveJobInfo): Mono<JobResult> {
        if (!canRun(activeJobInfo)) return Mono.empty()

        val text = activeJobInfo.prevActiveJobInfo?.jobResult?.value
                ?: (activeJobInfo.jobInfo?.execInfo as? JustSayJobExecInfo)?.text
                ?: return Mono.empty()

        speakService.say(text)

        val result = JobResult(text)
        return Mono.just(result)
    }


    private fun canRun(activeJobInfo: ActiveJobInfo) =
            activeJobInfo.jobInfo?.execInfo?.type == JobExecType.JUST_SAY
}
