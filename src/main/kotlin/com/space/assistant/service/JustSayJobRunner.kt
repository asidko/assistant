package com.space.assistant.service

import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.service.JobRunner
import com.space.assistant.core.service.SpeakService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JustSayJobRunner(
        private val speakService: SpeakService
) : JobRunner {

    override fun runJob(jobInfo: JobInfo): Mono<JobResult> {
        if (!canRun(jobInfo)) return Mono.empty()

        val textToSay = jobInfo.execValue.ifEmpty { jobInfo.parseValue }
        speakService.say(textToSay)

        return Mono.just(JobResult(textToSay, jobInfo))
    }

    private fun canRun(jobInfo: JobInfo) = jobInfo.execType == JobExecType.JUST_SAY
}
