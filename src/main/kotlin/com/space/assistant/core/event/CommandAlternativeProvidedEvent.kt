package com.space.assistant.core.event

import com.space.assistant.core.entity.ActiveJobEvent
import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.CommandAlternative

class CommandAlternativeProvidedEvent(
        activeJobInfo: ActiveJobInfo,
        val commandAlternative: CommandAlternative) : ActiveJobEvent(activeJobInfo)
