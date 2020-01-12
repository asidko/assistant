package com.space.assistant.core.event

import com.space.assistant.core.entity.ActiveJobEvent
import com.space.assistant.core.entity.ActiveJobInfo

class JobFinalResultProvidedEvent(activeJobInfo: ActiveJobInfo) : ActiveJobEvent(activeJobInfo)
