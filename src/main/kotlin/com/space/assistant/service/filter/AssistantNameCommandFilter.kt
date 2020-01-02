package com.space.assistant.service.filter

import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.entity.Phrase
import com.space.assistant.core.service.InputCommandFilter
import org.springframework.stereotype.Service

@Service
class AssistantNameCommandFilter : InputCommandFilter {
    val assistantName = "Чарли"

    val containsAssistantName: (Phrase) -> Boolean = { phrase -> assistantName in phrase }

    override fun apply(command: InputCommand): InputCommand? {
        if (containsAssistantName(command.phrase) || command.alternativePhrases.any(containsAssistantName)) {

            val alternativesWithAssistantName = listOf(command.phrase).filter(containsAssistantName) +
                    command.alternativePhrases.filter(containsAssistantName)

            val alternativesWithRemovedAssistantName = alternativesWithAssistantName
                    .map { phrase -> phrase.filter { it != assistantName } }

            return command.copy(
                    alternativePhrases = alternativesWithRemovedAssistantName + command.alternativePhrases
            )
        }
        return null
    }
}
