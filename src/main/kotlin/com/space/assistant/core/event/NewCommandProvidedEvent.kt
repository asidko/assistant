package com.space.assistant.core.event

import com.space.assistant.core.entity.ActiveJobInfo

data class NewCommandProvidedEvent(val activeJobInfo: ActiveJobInfo)
