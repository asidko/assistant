package com.space.assistant.core.event

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.CommandAlternative

data class CommandAlternativeProvidedEvent(
        val activeJobInfo: ActiveJobInfo,
        val commandAlternative: CommandAlternative)
