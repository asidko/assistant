package com.space.assistant.core.event

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobResult

data class JobFinalResultProvidedEvent(val jobResult: JobResult, val command: CommandAlternative)
