package com.space.assistant.core.service

import com.space.assistant.core.entity.JobResult
import com.space.assistant.core.entity.RunJobInfo
import reactor.core.publisher.Mono

interface JobRunner {
    fun runJob(runJobInfo: RunJobInfo): Mono<JobResult>
}
