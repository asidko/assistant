package com.space.assistant.core.service

interface VoiceRecognitionService {
    fun start(langCode: String, onResult: (List<String>) -> Unit)
    fun stop()
}