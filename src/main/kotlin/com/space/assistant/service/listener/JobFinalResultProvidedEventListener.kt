package com.space.assistant.service.listener

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.asArgs
import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.SpeakVoiceService
import com.space.assistant.service.text.VariablesInTextReplacer
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobFinalResultProvidedEventListener(
        private val activeJobManager: ActiveJobManager,
        private val variablesInTextReplacer: VariablesInTextReplacer,
        private val speakVoiceService: SpeakVoiceService) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    fun handleEvent(event: JobFinalResultProvidedEvent) {
        val activeJobInfo = event.activeJobInfo

        sayPostExecPhrase(activeJobInfo)

        activeJobManager.activateNextJob(activeJobInfo)
    }

    private fun sayPostExecPhrase(activeJobInfo: ActiveJobInfo) {
        val postExecPhrases = activeJobInfo.jobInfo?.phraseAfter ?: return
        val postExecPhrase = if (postExecPhrases.isNotEmpty()) postExecPhrases.random() else return

        val args = activeJobInfo.jobResult?.asArgs()
        val resultPhrase = variablesInTextReplacer.replacePattern(postExecPhrase, args)
        speakVoiceService.say(resultPhrase)
    }
}

