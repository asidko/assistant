package com.space.assistant.service.runner

import com.space.assistant.core.entity.*
import com.space.assistant.core.service.JobRunner
import com.space.assistant.core.service.SpeakVoiceService
import org.springframework.stereotype.Service

@Service
class JustSayJobRunner(
        private val speakVoiceService: SpeakVoiceService
) : JobRunner {
    companion object {
        const val typeName = "JUST_SAY"
    }

    data class Info(
            val text: String,
            override val type: String = typeName
    ) : JobRunnerInfo

    override suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult? {
        val execInfo = activeJobInfo.jobInfo?.runnerInfo as? Info ?: return null

        val prevJobResult = activeJobInfo.prevActiveJobInfo?.jobResult
        val textToSay = prevJobResult?.value ?: execInfo.text

        speakVoiceService.say(textToSay)

        return JobResult(textToSay)
    }
}
