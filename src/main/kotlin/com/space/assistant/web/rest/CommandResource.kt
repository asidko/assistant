package com.space.assistant.web.rest

import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.entity.fromText
import com.space.assistant.core.event.NewCommandProvidedEvent
import com.space.assistant.core.service.InputCommandFilter
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CommandResource(
        private val eventPublisher: ApplicationEventPublisher,
        private val filter: InputCommandFilter
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("command/{text}")
    fun handleCommand(@PathVariable text: String) {
        val command = InputCommand.fromText(text)

        val filtered = filter.apply(command)
        if (filtered == null) {
            log.debug("Command with text={} was filtered out", text)
            return
        }

        eventPublisher.publishEvent(NewCommandProvidedEvent(command))
    }
}
