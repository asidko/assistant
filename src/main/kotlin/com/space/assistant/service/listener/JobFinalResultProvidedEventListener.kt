package com.space.assistant.service.listener

import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.service.JobProvider
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobFinalResultProvidedEventListener(
        private val jobProvider: JobProvider,
        private val eventPublisher: ApplicationEventPublisher) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    fun handleEvent(event: JobFinalResultProvidedEvent) {
        val currentJobResult = event.jobResult
        log.debug("Current job result is {}", currentJobResult)

        val currentJobResultRedirects = currentJobResult.redirectToJobs
        val currentJobPresetRedirects = currentJobResult.jobInfo.redirectToJobs
        val allJobRedirects = currentJobPresetRedirects + currentJobResultRedirects
        log.debug("Current job preset redirects is {}", currentJobPresetRedirects)
        log.debug("Current job result redirects is {}", currentJobResultRedirects)

        log.debug("All job redirects is {}", allJobRedirects)
        if (allJobRedirects.isEmpty()) {
            log.debug("There is no more redirects. Return.")
            return
        }

        val nextJobUuid = allJobRedirects.first()
        log.debug("Looking for next job with uuid {}", nextJobUuid)

        val nextJob = jobProvider.findJob(nextJobUuid) ?: return
        log.debug("Found next job {}", nextJob)

        val otherJobRedirects = allJobRedirects.subList(1, allJobRedirects.size)
        val jobResultForNext = currentJobResult.copy(jobInfo = nextJob, redirectToJobs = otherJobRedirects)

        val nextJobEvent = JobProvidedEvent(nextJob, jobResultForNext)
        log.debug("Publishing event for next job {}", nextJobEvent)
        eventPublisher.publishEvent(nextJobEvent)
    }

}

