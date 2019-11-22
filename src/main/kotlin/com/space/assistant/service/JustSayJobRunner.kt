package com.space.assistant.service

import com.space.assistant.core.entity.JobExecType
import com.space.assistant.core.entity.JobInfo
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

    override fun runJob(jobInfo: JobInfo): Mono<JobResult> {
        if (!canRun(jobInfo)) return Mono.empty()

        val text = sayText(jobInfo)

        return Mono.just(JobResult(text, jobInfo))
    }

    private fun sayText(jobInfo: JobInfo): String {
        val text = (jobInfo.execInfo as JustSayJobExecInfo).text
        speakService.say(text)

        return text
    }

    private fun canRun(jobInfo: JobInfo) = jobInfo.execInfo.type == JobExecType.JUST_SAY
}
