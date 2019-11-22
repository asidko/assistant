package com.space.assistant.core.event

import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult
import org.springframework.context.ApplicationEvent

class JobRawResultProvidedEvent(val jobResult: JobResult)
