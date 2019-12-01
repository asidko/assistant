package com.space.assistant.service.listener

import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.service.JobRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobFinalResultProvidedEventListener(
        private val jobRepository: JobRepository,
        private val eventPublisher: ApplicationEventPublisher) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    fun handleEvent(event: JobFinalResultProvidedEvent) {
        val currentJobResult = event.jobResult
        log.debug("Current job result is {}", currentJobResult)

        val redirectsFromJobResult = currentJobResult.redirectToJobs
        log.debug("Redirects from job result is {}", redirectsFromJobResult)
        val redirectsFromJobInfo = currentJobResult.jobInfo.redirectToJobs
        log.debug("Redirects from job info is {}", redirectsFromJobInfo)

        val joinedRedirects = redirectsFromJobInfo + redirectsFromJobResult
        if (joinedRedirects.isEmpty()) {
            log.debug("There is no more redirects. Return.")
            return
        }

        val nextJobUuid = joinedRedirects.first()
        log.debug("Looking for next job with uuid {}", nextJobUuid)
        val nextJob = jobRepository.findJobByUuid(nextJobUuid) ?: return
        log.debug("Found next job {}", nextJob)

        val remainingRedirects = joinedRedirects.subList(1, joinedRedirects.size)
        val jobResultForNext = currentJobResult.copy(jobInfo = nextJob, redirectToJobs = remainingRedirects)
        val nextJobEvent = JobProvidedEvent(nextJob, jobResultForNext)

        log.debug("Publishing event for next job {}", nextJobEvent)
        eventPublisher.publishEvent(nextJobEvent)
    }

}

