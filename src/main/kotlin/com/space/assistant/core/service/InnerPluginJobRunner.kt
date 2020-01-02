package com.space.assistant.core.service

import com.space.assistant.core.entity.RunJobInfo
import reactor.core.publisher.Mono

interface InnerPluginJobRunner {
    fun runJob(runJobInfo: RunJobInfo): Mono<String>
}
