package com.space.assistant.web.rest

import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.entity.fromText
import com.space.assistant.core.event.NewCommandProvidedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CommandResource(
        private val eventPublisher: ApplicationEventPublisher
) {
    @GetMapping("command/{text}")
    fun handleCommand(@PathVariable text: String) {
        val command = InputCommand.fromText(text)
        val event = NewCommandProvidedEvent(command)
        eventPublisher.publishEvent(event)
    }
}
