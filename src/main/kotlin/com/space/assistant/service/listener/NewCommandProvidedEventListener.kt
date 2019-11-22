package com.space.assistant.service.listener

import com.space.assistant.core.event.CommandAlternativeProvidedEvent
import com.space.assistant.core.event.NewCommandProvidedEvent
import com.space.assistant.core.service.CommandAlternativeProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NewCommandProvidedEventListener(
        private val commandAlternativeProviders: List<CommandAlternativeProvider>,
        private val eventPublisher: ApplicationEventPublisher) {

    @EventListener
    fun handleEvent(event: NewCommandProvidedEvent) {
        for (provider in commandAlternativeProviders) {
            GlobalScope.launch {
                val alternatives = provider.getAlternatives(event.command)
                alternatives
                        .map(::CommandAlternativeProvidedEvent)
                        .forEach { event -> eventPublisher.publishEvent(event) }
            }
        }
    }
}
