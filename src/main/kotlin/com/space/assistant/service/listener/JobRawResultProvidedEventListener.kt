package com.space.assistant.service.listener

import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.event.JobRawResultProvidedEvent
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.core.service.JobRunner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class JobRawResultProvidedEventListener(
        private val jobResultParsers: List<JobResultParser>,
        private val eventPublisher: ApplicationEventPublisher) {

    @EventListener
    fun handleEvent(event: JobRawResultProvidedEvent) {
        for (parser in jobResultParsers) {
            GlobalScope.launch {
                parser.parseResult(event.jobResult)
            }
        }
    }
}
