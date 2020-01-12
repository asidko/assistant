package com.space.assistant.core.service

interface EventPublisher {
    fun publishEvent(event: Any)
}