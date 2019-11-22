package com.space.assistant.core.service

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.InputCommand

interface CommandAlternativeProvider {
    fun getAlternatives(inputCommand: InputCommand): List<CommandAlternative>
}
