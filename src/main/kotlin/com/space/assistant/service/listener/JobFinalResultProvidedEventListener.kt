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

@Component
class JobFinalResultProvidedEventListener(
        private val speakService: SpeakService,
        private val eventPublisher: ApplicationEventPublisher) {

    @EventListener
    fun handleEvent(event: JobFinalResultProvidedEvent) {
            val jobResult = event.jobResult
            if (shouldSay(jobResult)) say(jobResult)
    }

    private fun say(jobResult: JobResult) = speakService.say(jobResult.result)

    private fun shouldSay(jobResult: JobResult) = jobResult.jobInfo.shouldSayResult

}

