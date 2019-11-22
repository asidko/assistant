package com.space.assistant.service.listener

import com.space.assistant.core.event.CommandAlternativeProvidedEvent
import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.service.JobProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class CommandAlternativeProvidedEventListener(
        private val jobProviders: List<JobProvider>,
        private val eventPublisher: ApplicationEventPublisher) {

    @EventListener
    fun handleEvent(event: CommandAlternativeProvidedEvent) {
        for (provider in jobProviders) {
            GlobalScope.launch {
                val job = provider.findJob(event.command)
                if (job != null)
                    eventPublisher.publishEvent(JobProvidedEvent(job, null))
            }
        }
    }
}
