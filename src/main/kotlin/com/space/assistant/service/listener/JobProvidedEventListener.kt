package com.space.assistant.service.listener

import com.space.assistant.core.entity.RunJobInfo
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
                val runJobInfo = RunJobInfo(event.job, event.command, event.previousJobResult)
                val jobResultMono = runner.runJob(runJobInfo)

                jobResultMono
                        .map { jobResult -> JobRawResultProvidedEvent(jobResult, event.command) }
                        .subscribe { event -> eventPublisher.publishEvent(event) }
            }
        }
    }
}
