package com.space.assistant.service.listener

import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.event.JobRawResultProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.EventPublisher
import com.space.assistant.core.service.JobResultParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobRawResultProvidedEventListener(
        private val jobResultParsers: List<JobResultParser>,
        private val eventPublisher: EventPublisher,
        private val activeJobManager: ActiveJobManager) {

    @EventListener
    fun handleEvent(event: JobRawResultProvidedEvent) {
        for (parser in jobResultParsers) {
            GlobalScope.launch {
                val activeJobInfo = event.activeJobInfo
                val jobResult = parser.parseResult(activeJobInfo)

                val updatedActiveJobInfo = activeJobManager.setResult(activeJobInfo, jobResult)

                val newEvent = JobFinalResultProvidedEvent(updatedActiveJobInfo)
                eventPublisher.publishEvent(newEvent)
            }
        }
    }
}
