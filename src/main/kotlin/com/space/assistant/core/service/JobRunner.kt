package com.space.assistant.core.service

import com.space.assistant.core.entity.JobInfo
import com.space.assistant.core.entity.JobResult
import reactor.core.publisher.Mono

interface JobRunner {
    fun runJob(jobInfo: JobInfo, previousJobResult: JobResult?): Mono<JobResult>
}
