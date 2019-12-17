package com.space.assistant.service.listener

import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.event.JobRawResultProvidedEvent
import com.space.assistant.core.service.JobResultParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobRawResultProvidedEventListener(
        private val jobResultParsers: List<JobResultParser>,
        private val eventPublisher: ApplicationEventPublisher) {

    @EventListener
    fun handleEvent(event: JobRawResultProvidedEvent) {
        for (parser in jobResultParsers) {
            GlobalScope.launch {
                val resultMono = parser.parseResult(event.jobResult)

                resultMono
                        .map { JobFinalResultProvidedEvent(it, event.command) }
                        .subscribe { eventPublisher.publishEvent(it) }
            }
        }
    }
}
