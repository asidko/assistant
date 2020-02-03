package com.space.assistant.service.listener

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.asArgs
import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.SpeakService
import com.space.assistant.service.text.PatternStringReplacer
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobFinalResultProvidedEventListener(
        private val activeJobManager: ActiveJobManager,
        private val patternStringReplacer: PatternStringReplacer,
        private val speakService: SpeakService) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    fun handleEvent(event: JobFinalResultProvidedEvent) {
        val activeJobInfo = event.activeJobInfo

        sayPostExecPhrase(activeJobInfo)

        activeJobManager.tryNextJob(activeJobInfo)
    }

    private fun sayPostExecPhrase(activeJobInfo: ActiveJobInfo) {
        val postExecPhrases = activeJobInfo.jobInfo?.postExecPhrase ?: return
        val postExecPhrase = if (postExecPhrases.isNotEmpty()) postExecPhrases.random() else return

        val args = activeJobInfo.jobResult?.asArgs()
        val resultPhrase = patternStringReplacer.replacePattern(postExecPhrase, args)
        speakService.say(resultPhrase)
    }
}

