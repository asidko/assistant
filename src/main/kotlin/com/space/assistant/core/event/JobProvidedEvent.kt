package com.space.assistant.core.event

import com.space.assistant.core.entity.ActiveJobEvent
import com.space.assistant.core.entity.ActiveJobInfo

class JobProvidedEvent(activeJobInfo: ActiveJobInfo) : ActiveJobEvent(activeJobInfo)