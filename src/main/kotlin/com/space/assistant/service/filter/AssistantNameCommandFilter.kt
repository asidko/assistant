package com.space.assistant.service.filter

import com.space.assistant.config.AssistantProperties
import com.space.assistant.core.entity.InputCommand
import com.space.assistant.core.entity.Phrase
import com.space.assistant.core.service.InputCommandFilter
import org.springframework.stereotype.Service

@Service
class AssistantNameCommandFilter(
        private val props: AssistantProperties
) : InputCommandFilter {

    private val containsAssistantName: (Phrase) -> Boolean = { phrase -> props.assistantName in phrase }

    override fun apply(command: InputCommand): InputCommand? {
        return if (containsAssistantName(command.phrase) ||
                command.alternativePhrases.any(containsAssistantName)) command
        else null
    }
}
