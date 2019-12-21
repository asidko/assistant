package com.space.assistant.service.alternative

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.service.CommandAlternativeProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class DirectCommandAlternativeProvider : CommandAlternativeProvider {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun getAlternatives(inputCommand: InputCommand): List<CommandAlternative> {
        val allPhrases = mutableListOf(inputCommand.phrase) + inputCommand.alternativePhrases
        log.debug("Providing alternatives {}", allPhrases)

        return allPhrases.map { phrase ->
            CommandAlternative(
                    inputCommandUUID = inputCommand.uuid,
                    alternativePhrase = phrase)
        }
    }

}
