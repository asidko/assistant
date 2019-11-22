package com.space.assistant.service.listener

import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.event.JobFinalResultProvidedEvent
import com.space.assistant.core.event.JobRawResultProvidedEvent
import com.space.assistant.core.service.SpeakService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class JobFinalResultProvidedEventListener(
        private val speakService: SpeakService,
        private val eventPublisher: ApplicationEventPublisher) {

    @EventListener
    fun handleEvent(event: JobFinalResultProvidedEvent) {
            val jobResult = event.jobResult
    }



}

