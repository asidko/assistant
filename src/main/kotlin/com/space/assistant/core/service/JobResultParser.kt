package com.space.assistant.core.service

import com.space.assistant.core.entity.JobResult
import reactor.core.publisher.Mono

interface JobResultParser {
    fun parseResult(jobRawResult: JobResult): Mono<JobResult>
}
