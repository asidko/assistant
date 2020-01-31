package com.space.assistant.service.alternative

import com.space.assistant.config.AssistantProperties
import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.entity.Phrase
import com.space.assistant.core.service.CommandAlternativeProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class AssistantNameAlternativeProvider(
        private val props: AssistantProperties
) : CommandAlternativeProvider {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val containsAssistantName: (Phrase) -> Boolean = { phrase -> props.assistantName in phrase }

    override fun getAlternatives(inputCommand: InputCommand): List<CommandAlternative> {
        val phrasesWithAssistantName =
                listOf(inputCommand.phrase).filter(containsAssistantName) +
                        inputCommand.alternativePhrases.filter(containsAssistantName)

        if (phrasesWithAssistantName.isNotEmpty())
            log.debug("Found {} phrases with assistant name. Removing assistant name", phrasesWithAssistantName.size)

        val phrasesWithRemovedAssistantName = phrasesWithAssistantName
                .map { phrase -> phrase.filter { it != props.assistantName } }

        return phrasesWithRemovedAssistantName.map { phrase ->
            CommandAlternative(
                    inputCommandUUID = inputCommand.uuid,
                    alternativePhrase = phrase)
        }
    }

}
