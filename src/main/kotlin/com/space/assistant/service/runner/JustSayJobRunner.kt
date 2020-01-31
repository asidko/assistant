package com.space.assistant.service.runner

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.JustSayJobExecInfo
import com.space.assistant.core.service.JobRunner
import com.space.assistant.core.service.SpeakService
import org.springframework.stereotype.Service

@Service
class JustSayJobRunner(
        private val speakService: SpeakService
) : JobRunner {
    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.execInfo as? JustSayJobExecInfo ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val textToSay = prevJobResult?.value ?: execInfo.text

        speakService.say(textToSay)

        return JobResult(textToSay)
    }
}
