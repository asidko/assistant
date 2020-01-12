package com.space.assistant.service

import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.VoiceRecognitionService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class VoiceRecognitionApplicationListener(
        private val voiceRecognitionService: VoiceRecognitionService,
        private val activeJobManager: ActiveJobManager
) {

    @EventListener
    fun onApplicationEvent(event: ApplicationReadyEvent) {
        print("Application started!")
        Thread {
            voiceRecognitionService.start("ru-RU") { activeJobManager.tryNewJobs(it) }
        }.start()
    }
}