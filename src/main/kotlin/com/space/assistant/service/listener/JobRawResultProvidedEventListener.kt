package com.space.assistant.service.listener

import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.event.JobRawResultProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.EventPublisher
import com.space.assistant.core.service.JobResultParser
import com.space.assistant.core.service.ServicesContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobRawResultProvidedEventListener(
        private val servicesContext: ServicesContext,
        private val eventPublisher: EventPublisher,
        private val activeJobManager: ActiveJobManager) {

    @EventListener
    fun handleEvent(event: JobRawResultProvidedEvent) {
        GlobalScope.launch {
            val activeJobInfo = event.activeJobInfo
            val parserInfo = activeJobInfo.jobInfo?.resultParserInfo ?: return@launch
            val parser = servicesContext.getServiceByInfo(parserInfo) as? JobResultParser
                    ?: return@launch

            val jobResult = parser.parseResult(activeJobInfo) ?: return@launch
            val updatedActiveJobInfo = activeJobManager.setResult(activeJobInfo, jobResult)

            val newEvent = JobFinalResultProvidedEvent(updatedActiveJobInfo)
            eventPublisher.publishEvent(newEvent)
        }
    }
}
