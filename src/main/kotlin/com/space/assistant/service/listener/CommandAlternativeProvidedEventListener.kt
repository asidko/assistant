package com.space.assistant.service.listener

import com.space.assistant.core.event.CommandAlternativeProvidedEvent
import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.EventPublisher
import com.space.assistant.core.service.JobSearchProvider
import com.space.assistant.core.service.SpeakService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class CommandAlternativeProvidedEventListener(
        private val jobSearchProviders: List<JobSearchProvider>,
        private val activeJobManager: ActiveJobManager,
        private val eventPublisher: EventPublisher,
        private val speakService: SpeakService) {

    @EventListener
    fun handleEvent(event: CommandAlternativeProvidedEvent) {
        for (provider in jobSearchProviders) {
            GlobalScope.launch {
                var activeJobInfo = event.activeJobInfo
                val commandAlternative = event.commandAlternative

                val jobInfo = provider.findJob(commandAlternative) ?: return@launch
                activeJobInfo = activeJobManager.setJobInfo(activeJobInfo, jobInfo)
                activeJobInfo = activeJobManager.setAlternativeSucceed(activeJobInfo, commandAlternative)

                eventPublisher.publishEvent(JobProvidedEvent(activeJobInfo))

                if (jobInfo.phraseBefore.isNotEmpty())
                    speakService.say(jobInfo.phraseBefore.random())

            }
        }
    }
}
