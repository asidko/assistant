package com.space.assistant.service.listener

import com.space.assistant.core.event.CommandAlternativeProvidedEvent
import com.space.assistant.core.event.NewCommandProvidedEvent
import com.space.assistant.core.service.ActiveJobManager
import com.space.assistant.core.service.CommandAlternativeProvider
import com.space.assistant.core.service.EventPublisher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class NewCommandProvidedEventListener(
        private val commandAlternativeProviders: List<CommandAlternativeProvider>,
        private val activeJobManager: ActiveJobManager,
        private val eventPublisher: EventPublisher) {

    @EventListener
    fun handleEvent(event: NewCommandProvidedEvent) {
        for (provider in commandAlternativeProviders) {
            GlobalScope.launch {
                var activeJobInfo = event.activeJobInfo
                val command = activeJobInfo.inputCommand ?: return@launch

                val commandAlternatives = provider.getAlternatives(command)
                activeJobInfo = activeJobManager.addAlternatives(activeJobInfo, commandAlternatives)

                commandAlternatives
                        .map { alternative -> CommandAlternativeProvidedEvent(activeJobInfo, alternative) }
                        .forEach { event -> delay(10000); eventPublisher.publishEvent(event) }
            }
        }
    }
}
