package com.space.assistant.service.listener

import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.event.JobRawResultProvidedEvent
import com.space.assistant.core.service.JobRunner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobProvidedEventListener(
        private val jobRunners: List<JobRunner>,
        private val eventPublisher: ApplicationEventPublisher) {

    @EventListener
    fun handleEvent(event: JobProvidedEvent) {
        for (runner in jobRunners) {
            GlobalScope.launch {
                val jobResultMono = runner.runJob(event.job, event.previousJobResult)

                jobResultMono
                        .map { jobResult -> JobRawResultProvidedEvent(jobResult) }
                        .subscribe { event -> eventPublisher.publishEvent(event) }
            }
        }
    }
}
