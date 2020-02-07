package com.space.assistant.service.listener.application

import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.ListenVoiceService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.concurrent.thread


@Component
class VoiceRecognitionApplicationListener(
        private val listenVoiceService: ListenVoiceService,
        private val activeJobManager: ActiveJobManager
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    fun onEvent(event: ApplicationReadyEvent) {
        startVoiceRecognition()
    }

    private fun startVoiceRecognition() {
        log.info("Starting Voice recognition")

        thread(true) {
            listenVoiceService.start("ru-RU") { activeJobManager.activateJob(it) }
        }
    }
}
