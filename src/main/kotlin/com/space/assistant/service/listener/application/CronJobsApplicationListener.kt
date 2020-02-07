package com.space.assistant.service.listener.application

import com.space.assistant.core.service.JobRepository
import com.space.assistant.service.CronJobService
import com.space.assistant.service.search.CronJobActivator
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class CronJobsApplicationListener(
        private val jobRepository: JobRepository,
        private val cronJobService: CronJobService
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    fun onEvent(event: ApplicationReadyEvent) {
        loadCronActivatorJobs()
    }

    private fun loadCronActivatorJobs() {
        log.info("Registering Cron jobs")

        jobRepository
                .findJobsByActivatorType(CronJobActivator.typeName)
                .forEach { cronJobService.registerJob(it) }
    }
}
