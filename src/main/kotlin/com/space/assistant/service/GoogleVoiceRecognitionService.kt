package com.space.assistant.service

import com.space.assistant.core.service.VoiceRecognitionService
import org.springframework.stereotype.Service

@Service
class GoogleVoiceRecognitionService : VoiceRecognitionService {
    var googleRecognition: GoogleInfiniteStreamRecognize? = null

    override fun start(langCode: String, onResult: (String) -> Unit) {
        googleRecognition = GoogleInfiniteStreamRecognize(onResult)
        googleRecognition?.start("ru-RU")
    }

    override fun stop() {
        googleRecognition?.stop()
    }
}