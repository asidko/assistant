package com.space.assistant.service

import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.EventPublisher
import com.space.assistant.core.service.JobRunner
import com.space.assistant.service.search.CronJobActivator
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Service


@Service
@Lazy
class CronJobService(private val executor: TaskScheduler,
                     private val context: ApplicationContext,
                     private val activeJobManager: ActiveJobManager) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun registerJob(jobInfo: JobInfo) {
        val activatorInfo = jobInfo.activatorInfo as? CronJobActivator.Info ?: return

        try {
            executor.schedule(Runnable {
                activeJobManager.activateJob(jobInfo)
            }, CronTrigger(activatorInfo.cron))
        } catch (e: Exception) {
            log.error("Error when try to schedule cron job {}", jobInfo.uuid, e)
        }

        log.info("Register cron job {} on {}", jobInfo.uuid, activatorInfo.cron)
    }
}
