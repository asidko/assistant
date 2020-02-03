package com.space.assistant.service

import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.VoiceRecognitionService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Component
class ApplicationReadyEventListener(
        private val voiceRecognitionService: VoiceRecognitionService,
        private val activeJobManager: ActiveJobManager
) {

    @EventListener
    fun onApplicationEvent(event: ApplicationReadyEvent) {
        startVoiceRecognition()
    }

    private fun startVoiceRecognition() {
        print("Application started!")
        thread(true) {
            voiceRecognitionService.start("ru-RU") { activeJobManager.tryNewJobs(it) }
        }
    }
}
