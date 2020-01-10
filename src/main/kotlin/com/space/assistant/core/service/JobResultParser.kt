package com.space.assistant.core.service

import com.space.assistant.core.entity.ActiveJobInfo
import com.space.assistant.core.entity.JobResult
import reactor.core.publisher.Mono

interface JobResultParser {
    fun parseResult(activeJobInfo: ActiveJobInfo): Mono<JobResult>
}
