package com.space.assistant.service.runner

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import com.space.assistant.core.service.SpeakService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JustSayJobRunner(
        private val speakService: SpeakService
) : JobRunner {

    override fun runJob(runJobInfo: RunJobInfo): Mono<JobResult> {
        if (!canRun(runJobInfo.jobInfo)) return Mono.empty()

        val text = runJobInfo.previousJobResult?.result
                ?: (runJobInfo.jobInfo.execInfo as JustSayJobExecInfo).text

        speakService.say(text)

        val result = runJobInfo.previousJobResult?.copy(result = text)
                ?: JobResult.new(text, runJobInfo.jobInfo)

        return Mono.just(result)
    }


    private fun canRun(jobInfo: JobInfo) = jobInfo.execInfo.type == JobExecType.JUST_SAY
}
