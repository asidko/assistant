package com.space.assistant.service

import com.space.assistant.core.entity.ActiveJobEvent
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.event.CommandAlternativeProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class GuardEventPublisher(
        private val eventPublisher: ApplicationEventPublisher,
        @Lazy private val activeJobManager: ActiveJobManager
) : EventPublisher {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun publishEvent(event: Any) {
        val shouldDeny = event is ActiveJobEvent && shouldDeny(event)

        if (shouldDeny) {
            log.debug("Event {} is denied", event::class.simpleName)
            return
        }

        eventPublisher.publishEvent(event)
    }

    private fun shouldDeny(event: ActiveJobEvent): Boolean {
        val lastActiveJobInfo = activeJobManager.getActiveJob(event.activeJobInfo.uuid)
                ?: return false

        if (event is CommandAlternativeProvidedEvent && isJobAlreadyFound(lastActiveJobInfo))
            return true


        return false
    }

    private fun isJobAlreadyFound(activeJobInfo: ActiveJobInfo) = activeJobInfo.jobInfo != null
}
