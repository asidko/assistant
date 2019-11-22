package com.space.assistant.service

import com.space.assistant.core.service.SpeakService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FakeSpeakService : SpeakService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun say(text: String) {
        log.info("Speaking: $text")
    }
}
