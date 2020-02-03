package com.space.assistant.core.service

interface ListenVoiceService {
    fun start(langCode: String, onResult: (List<String>) -> Unit)
    fun stop()
}
