package com.space.assistant.core.event

import com.space.assistant.core.entity.CommandAlternative
import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult

data class JobProvidedEvent(val job: JobInfo, val command: CommandAlternative, val previousJobResult: JobResult?)
