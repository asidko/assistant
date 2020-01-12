package com.space.assistant.config

import org.springframework.stereotype.Component

@Component
class AssistantProperties {
    val assistantName get() = "Фрэнк"
    val shouldCallByName get() = false
}
