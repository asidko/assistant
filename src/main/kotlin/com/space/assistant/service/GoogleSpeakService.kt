package com.space.assistant.service

import com.space.assistant.core.service.SpeakService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GoogleSpeakService(
        private val googleSpeech: GoogleSpeechSynthesis
) : SpeakService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Synchronized
    override fun say(text: String) {
        log.info("Speaking: $text")
        googleSpeech.say(text)
    }
}
