package com.space.assistant.service.listener

import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class JobFinalResultProvidedEventListener(
        private val activeJobManager: ActiveJobManager) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    fun handleEvent(event: JobFinalResultProvidedEvent) {
        val activeJobInfo = event.activeJobInfo
        activeJobManager.tryNextJob(activeJobInfo)
    }
}

