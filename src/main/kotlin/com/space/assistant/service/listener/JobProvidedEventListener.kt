package com.space.assistant.service.listener

import com.space.assistant.core.event.JobProvidedEvent
import com.space.assistant.core.event.JobRawResultProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.EventPublisher
import com.space.assistant.core.service.JobRunner
import com.space.assistant.core.service.ServicesContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobProvidedEventListener(
        private val activeJobManager: ActiveJobManager,
        private val servicesContext: ServicesContext,
        private val eventPublisher: EventPublisher) {

    @EventListener
    fun handleEvent(event: JobProvidedEvent) {
        GlobalScope.launch {
            val activeJobInfo = event.activeJobInfo
            val runnerInfo = activeJobInfo.jobInfo?.runnerInfo ?: return@launch
            val runner = servicesContext.getServiceByInfo(runnerInfo) as? JobRunner ?: return@launch

            val jobResult = runner.runJob(activeJobInfo) ?: return@launch
            val updatedActiveJobInfo = activeJobManager.setRawResult(activeJobInfo, jobResult)

            val newEvent = JobRawResultProvidedEvent(updatedActiveJobInfo)
            eventPublisher.publishEvent(newEvent)
        }
    }
}
