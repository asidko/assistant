package com.space.assistant.config

import org.springframework.stereotype.Component

@Component
class AssistantProperties {
    val assistantName get() = "Чарли"
    val shouldCallByName get() = false
}
