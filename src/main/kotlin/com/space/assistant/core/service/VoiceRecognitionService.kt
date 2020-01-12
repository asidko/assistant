package com.space.assistant.core.service

interface VoiceRecognitionService {
    fun start(langCode: String, onResult: (String) -> Unit)
    fun stop()
}