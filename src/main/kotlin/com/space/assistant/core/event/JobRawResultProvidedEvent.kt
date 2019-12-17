package com.space.assistant.core.event

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobResult

data class JobRawResultProvidedEvent(val jobResult: JobResult, val command: CommandAlternative)
