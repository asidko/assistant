package com.space.assistant.core.service

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult

interface JobRunner {
    suspend fun runJob(activeJobInfo: ActiveJobInfo): JobResult?
}
