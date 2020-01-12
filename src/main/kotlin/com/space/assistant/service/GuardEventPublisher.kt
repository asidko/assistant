package com.space.assistant.service

import com.space.assistant.core.entity.ActiveJobEvent
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

    val suppressingEventsWhenJobFound = mapOf(
            CommandAlternativeProvidedEvent::class to 1
    )

    override fun publishEvent(event: Any) {
        if (suppressEvent(event)) return
        eventPublisher.publishEvent(event)
    }

    private fun suppressEvent(event: Any): Boolean {
        if (event !is ActiveJobEvent) return false

        val isSuppressed = shouldSuppressWhenJobFound(event)

        if (isSuppressed) {
            log.debug("Event {} is suppressed", event::class.simpleName)
            return true
        }

        return false
    }


    private fun shouldSuppressWhenJobFound(currentEvent: ActiveJobEvent): Boolean {
        val activeJobInfo = currentEvent.activeJobInfo
        val freshActiveJobInfo = activeJobManager.getActiveJob(activeJobInfo.uuid) ?: return false

        if (freshActiveJobInfo.jobInfo == null) return false

        val hasDenyClass = currentEvent::class in suppressingEventsWhenJobFound
        if (hasDenyClass) log.debug("Suppressing event {} due to job {} already found", currentEvent::class.simpleName, freshActiveJobInfo.jobInfo.uuid)

        return hasDenyClass
    }
}